package a_plugin_struts;

import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.IWorkbenchPreferencePage;

import a_plugin_struts.StrutsProperties.STRUTS_SETTINGS;
import socialdear.views.ImplementedPreferences;
import socialdear.views.SettingsEnum;

public class StrutsProperties extends ImplementedPreferences<STRUTS_SETTINGS> implements IWorkbenchPreferencePage {

	public StrutsProperties() {
		// constructor
	}

	public enum STRUTS_SETTINGS implements SettingsEnum {
		FILES_BLACKLIST, REGISTRY, ANGULAR, REST, PATHS, ANGULAR_MODULE, ANGULAR_ROUTE, CUSTOMERS, GRAPH_VIZ_DEPTHS() {

			@Override
			public Object getDefault() {
				return 10;
			}
		},
		XSL_File
	}

	@Override
	public STRUTS_SETTINGS[] getRequiredValues() {
		return new STRUTS_SETTINGS[] {};
	}

	@Override
	protected void createFieldEditors() {
		addTField(FIELD_TYPE.STRING, STRUTS_SETTINGS.FILES_BLACKLIST, "File Blacklist (comma separated)");
		addTField(FIELD_TYPE.NUMBER, STRUTS_SETTINGS.GRAPH_VIZ_DEPTHS, "generated graph depth");
	}

	public static List<String> getFileBlackList() {
		return Arrays.asList(getValue(STRUTS_SETTINGS.FILES_BLACKLIST).split(","));
	}
}
