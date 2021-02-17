package views.launcher;

import cfg.SystemSettings;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.ProjectSelectionComponent;

public class LauncherView extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {
		return new ProjectSelectionComponent(LauncherContainerComponentWrapper.class);
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return SystemSettings.class;
	}

}
