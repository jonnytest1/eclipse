package a_plugin_struts.views;

import a_plugin_struts.StrutsProperties;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.ProjectSelectionComponent;

public class StrutsViewPart extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {

		return new ProjectSelectionComponent(StrutsProjectPage.class);
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return StrutsProperties.class;
	}

}
