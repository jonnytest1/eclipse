package views.logging;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.launcher.Branch;
import socialdear.views.component.CustomElementPanel;

public class LoggingElementView extends CustomElementPanel {

	LoggingElementViewMessagesPanel messagesContainer;

	private static final long serialVersionUID = 1L;

	private static List<Branch> branchNames = new ArrayList<>();

	private static LoggingElementView instance;

	String currentBranch = "localhost";

	JScrollPane scrollPane;

	public LoggingElementView() {
		setLayout(new BorderLayout(1, 2));
		recreate();
		instance = this;
	}

	@Override
	protected void addElements() {
		addFilters();
		if (currentBranch != null) {
			messagesContainer(currentBranch);
		}

	}

	private void messagesContainer(String branchName) {

		LoggingElementViewMessagesPanel messageArea = new LoggingElementViewMessagesPanel(branchName, this);
		messagesContainer = messageArea;

		scrollPane = new JScrollPane(messageArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		scrollPane.setAlignmentY(TOP_ALIGNMENT);
		scrollPane.doLayout();
		add(scrollPane);

	}

	private JComboBox<String> addFilters() {
		JComboBox<String> instances = new JComboBox<>();

		for (Branch branch : branchNames) {
			instances.addItem(branch.getName());
		}
		if (currentBranch != null) {
			instances.setSelectedItem(currentBranch);
		}
		instances.addActionListener(e -> {
			String branch = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
			currentBranch = branch;
			recreate();
		});
		add(instances, BorderLayout.PAGE_START);
		return instances;
	}

	/**
	 * @param branchNames the branchNames to set
	 */
	public static void setBranchNames(List<Branch> branchNames) {
		LoggingElementView.branchNames = branchNames;
		if (instance != null) {
			instance.recreate();
		}

	}

	public static void addBranch(String branchName) {
		if (LoggingElementView.branchNames.stream().noneMatch(b -> b.getName().equals(branchName))) {
			LoggingElementView.branchNames.add(new Branch(branchName, new ArrayList<>()));
			if (instance != null) {
				instance.recreate();
			}
		}
	}

}
