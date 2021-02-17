package socialdear.views.component.implemented.filter;

import java.awt.BorderLayout;
import java.util.Comparator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.CustomScrollPanelComponent;

public class TextFilterCoponent extends CustomElementPanel implements DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3262396107489157201L;

	private JTextArea textFilter;
	private TextFilterListComponent addingPanel;

	private List<FilterableComponent> components;

	public TextFilterCoponent(List<FilterableComponent> components) {
		this.components = components;
		setLayout(new BorderLayout());

	}

	@Override
	protected void addElements() {
		textFilter = new JTextArea();
		textFilter.getDocument().addDocumentListener(this);
		add(textFilter, BorderLayout.NORTH);

		addingPanel = new TextFilterListComponent(components);
		JScrollPane pane = new CustomScrollPanelComponent(addingPanel);
		addingPanel.setScrollBar(pane.getHorizontalScrollBar());
		add(pane, BorderLayout.CENTER);
	}

	/**
	 * @since 3.0
	 */
	public <T extends FilterableComponent> void append(T c, Comparator<T> comparator) {
		addingPanel.append(c, comparator);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		addingPanel.setFilter(textFilter.getText());

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		addingPanel.setFilter(textFilter.getText());

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		addingPanel.setFilter(textFilter.getText());

	}

}
