package views.current.widgets.userstories;

import java.awt.BorderLayout;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import socialdear.views.component.implemented.customizable.ConfigurationPanel;
import socialdear.views.component.implemented.customizable.CustomizableViewPanel;
import views.current.widgets.bugview.BugViewOptions.LABELS;

/**
 * @since 2.1
 */
public class UserStoryWidget extends CustomizableViewPanel {

	private static final long serialVersionUID = 1L;

	public UserStoryWidget() {
		setLayout(new BorderLayout());
	}

	@Override
	protected void addElements() {

		JScrollPane scrollPane = getScrollPane(new UserStoryListView(getPanelOptions()));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

	}

	@Override
	public ConfigurationPanel getConfigurationOptions() {
		return new UserStoryOptions(getPanelOptions());
	}

	@Override
	public JsonObject getDefaultPanelOptions() {
		return Json.createObjectBuilder().add(LABELS.FILTER.name(), "teamIteration is current")
				.add(LABELS.SORTING.name(), "NumericPriority").build();
	}

}
