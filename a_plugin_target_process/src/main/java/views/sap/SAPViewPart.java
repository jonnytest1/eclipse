package views.sap;

import cfg_tp.SystemSettings;
import service.SAPListener;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.ProjectSelectionComponent;

/**
 * @since 2.1
 */
public class SAPViewPart extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {
		ProjectSelectionComponent projectSelectionComponent = new ProjectSelectionComponent(SAPEntriesComponent.class,
				p -> SAPListener.getEntries().stream() //
						.filter(entry -> entry.getProject() == p).count() > 0);
		SAPListener.setProjectSelection(projectSelectionComponent);
		return projectSelectionComponent;
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return SystemSettings.class;
	}

}
