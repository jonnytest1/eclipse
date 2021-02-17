package views.current;

import cfg_tp.SystemSettings;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class CurrentlyActiveViewPart extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {

		return new CurrentlyActiveView();

	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return SystemSettings.class;
	}

}
