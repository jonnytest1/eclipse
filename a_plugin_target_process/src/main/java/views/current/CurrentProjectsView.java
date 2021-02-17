package views.current;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import cfg_tp.SystemSettings;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.customizable.CustomizableViewContainer;
import socialdear.views.component.implemented.customizable.CustomizableViewPanel;
import views.current.widgets.ProjectViewWidget;
import views.current.widgets.bugview.BugViewWidget;
import views.current.widgets.userstories.UserStoryWidget;

/**
 * @since 2.1
 */
public class CurrentProjectsView extends CustomElementPanel {

	private static final long serialVersionUID = 1L;

	CurrentProjectsView() {
		setLayout(new BorderLayout());
	}

	@Override
	protected void addElements() {

		List<CustomizableViewPanel> els = SystemSettings.getCustomizableView();

		add(new CustomizableViewContainer(els, SystemSettings::setCustomizableView,
				Arrays.asList(BugViewWidget.class, ProjectViewWidget.class, UserStoryWidget.class)),
				BorderLayout.CENTER);

	}

}
