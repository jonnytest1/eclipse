package socialdear.views.component.autocomplete;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class AutoCompleteTextField extends JTextField implements DocumentListener {

	private static final long serialVersionUID = -818254349606789886L;
	private static final String COMMIT_ACTION = "commit";

	private enum Mode {
		INSERT, COMPLETION
	}

	public interface IChangeListener {
		void change(String newText);
	}

	private final transient List<ComparableText> completeList;

	private transient List<ComparableText> filteredList;
	private Mode mode = Mode.INSERT;

	private ComparableText currentSuggestion;
	private IChangeListener onchange;

	public AutoCompleteTextField(List<ComparableText> keywords, IChangeListener onchange) {

		this.onchange = onchange;
		setFocusTraversalKeysEnabled(false);

		getDocument().addDocumentListener(this);

		getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
		getActionMap().put(COMMIT_ACTION, new CommitAction());

		completeList = keywords;
		filteredList = keywords;
		Collections.sort(keywords);
	}

	@Override
	public void changedUpdate(DocumentEvent ev) {
		if (getText().isEmpty()) {
			filteredList = completeList;
		}
		onchange.change(getText());
	}

	@Override
	public void removeUpdate(DocumentEvent ev) {
		if (getText().isEmpty()) {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			boolean isFromReplace = false;
			for (StackTraceElement stackE : stack) {
				if (stackE.getClassName().equals(CompletionTask.class.getName())
						|| stackE.getClassName().equals(CommitAction.class.getName())) {
					isFromReplace = true;
				}
			}
			if (!isFromReplace) {
				filteredList = completeList;
			}
		}
		onchange.change(getText());
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1)
			return;

		int pos = ev.getOffset();
		String content = null;
		try {
			content = getText(0, pos + 1);
		} catch (BadLocationException e) {
			return;
		}
		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				if (content.charAt(w) == '.') {
					w--;
				}
				break;
			}
		}

		if (filteredList.size() > 10 && pos - w < 2) {
			return;
		}

		String prefix = content.substring(w + 1);
		int n = Collections.binarySearch(filteredList, new ComparableText(prefix));
		if (n < 0 && -n <= filteredList.size()) {
			ComparableText match = filteredList.get(-n - 1);
			if (match.keyword.startsWith(prefix)) {

				currentSuggestion = match;
				// A completion is found
				String completion = match.keyword.substring(pos - w);

				SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}

		onchange.change(getText());
	}

	public class CommitAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5794543109646743416L;

		@Override
		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {

				filteredList = completeList.stream().filter(currentSuggestion::canBeNExt).collect(Collectors.toList());
				currentSuggestion = null;
				int pos = getSelectionEnd();
				StringBuilder sb = new StringBuilder(getText());
				// sb.insert(pos, " ");
				setText(sb.toString());
				setCaretPosition(pos);
				mode = Mode.INSERT;
			} else {
				replaceSelection("\t");
			}
		}
	}

	private class CompletionTask implements Runnable {
		private String completion;
		private int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder(getText());
			sb.insert(position, completion);
			setText(sb.toString());
			setCaretPosition(position + completion.length());
			moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}

}