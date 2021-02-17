package views.launcher;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.core.resources.IProject;

import socialdear.views.component.implemented.CustomScrollPanelComponent;
import socialdear.views.component.implemented.ProjectSelectionReceiver;

public class LauncherContainerComponentWrapper extends ProjectSelectionReceiver implements DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textFilter;
	private LauncherContainerComponent addingPanel;

	public LauncherContainerComponentWrapper() {
		setLayout(new BorderLayout());

	}

	@Override
	public void setProject(IProject project) {
		if (addingPanel != null) {
			addingPanel.setActiveProjectName(project.getName());
		}

	}

	@Override
	protected void addElements() {
		textFilter = new JTextArea();
		textFilter.getDocument().addDocumentListener(this);
		add(textFilter, BorderLayout.NORTH);

		addingPanel = new LauncherContainerComponent();
		JScrollPane pane = new CustomScrollPanelComponent(addingPanel);
		add(pane, BorderLayout.CENTER);
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
