package views;

import org.eclipse.core.resources.IProject;

import cfg.BuildSettings;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.ProjectSelectionComponent;

public class CustomBuildViewPart extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {
		return new ProjectSelectionComponent(CustomBuildView.class, this::displayProject);
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {

		return BuildSettings.class;
	}

	private boolean displayProject(IProject project) {
		return project.getFile("site.xml").exists();
	}

}
