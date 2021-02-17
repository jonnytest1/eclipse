package views.current.widgets.bugview;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import socialdear.views.component.implemented.customizable.ConfigurationPanel;
import socialdear.views.component.implemented.customizable.CustomizableViewPanel;

/**
 * @since 2.1
 */
public class BugViewWidget extends CustomizableViewPanel {

	private static final long serialVersionUID = 7397006273645287093L;

	public BugViewWidget() {
		setLayout(new BorderLayout());
	}

	@Override
	protected void addElements() {

		JScrollPane scrollPane = getScrollPane(new BugListView(getPanelOptions()));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

	}

	@Override
	public ConfigurationPanel getConfigurationOptions() {
		return new BugViewOptions(getPanelOptions());
	}

}
