package views.logging;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;

import model.launcher.ELKLoggingModel;
import socialdear.views.component.CustomElementPanel;

public class LoggingElementViewMessagesPanel extends CustomElementPanel implements ComponentListener {

	private static Map<String, List<ELKLoggingModel>> logEntries = new HashMap<>();

	private static final long serialVersionUID = 1752234243195784770L;

	private String branchName;

	private static LoggingElementViewMessagesPanel instance = null;

	private boolean isHighlighted = false;

	private LoggingElementView parent;

	public LoggingElementViewMessagesPanel(String branchName, LoggingElementView parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.branchName = branchName;

		instance = this;
		addComponentListener(this);
		addElements();
	}

	static List<ELKLoggingModel> getLogEntryList(String branch) {
		if (!logEntries.containsKey(branch)) {
			logEntries.put(branch, new ArrayList<>());
			LoggingElementView.addBranch(branch);
			if (instance != null) {
				instance.recreate();
			}
		}
		return logEntries.get(branch);
	}

	public static void addLogEntry(String branch, ELKLoggingModel entry) {
		if (instance != null) {
			getLogEntryList(branch).add(entry);
			if (branch.equals(instance.branchName))
				instance.addLogEntryElements(Arrays.asList(entry));
		}
	}

	public static void addLogEntries(String branch, List<ELKLoggingModel> newEntries) {
		if (instance != null) {
			getLogEntryList(branch).addAll(newEntries);
			if (branch.equals(instance.branchName)) {
				instance.addLogEntryElements(newEntries);
			}
		}
	}

	public void addLogEntryElements(List<ELKLoggingModel> newEntries) {
		newEntries.forEach(entry -> createLoggingElement(entry, parent.getSize()));
	}

	private void createLoggingElement(ELKLoggingModel current, Dimension dimension) {

		SingleLoggingElementPanel messageArea = new SingleLoggingElementPanel(current, dimension, isHighlighted);

		isHighlighted = !isHighlighted;

		add(messageArea);
		revalidate();
		repaint();

	}

	@Override
	public void recreate() {
		removeAll();
		addElements();
		revalidate();
		repaint();
	}

	@Override
	protected void addElements() {

		getLogEntryList(branchName).forEach(t -> {
			createLoggingElement(t, parent.getSize());
		});

	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (getLogEntryList(branchName).stream().noneMatch(elkmodel -> elkmodel.isReferencedComponentClicked())) {
			parent.scrollPane.getVerticalScrollBar()
					.setValue(parent.scrollPane.getVerticalScrollBar().getMaximum() + 1000);
			parent.scrollPane.revalidate();
			parent.scrollPane.repaint();
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

}
