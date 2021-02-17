package cfg;

import cfg.BuildSettings.PROPERTIES;
import socialdear.views.ImplementedPreferences;
import socialdear.views.SettingsEnum;

public class BuildSettings extends ImplementedPreferences<PROPERTIES> {

	public enum PROPERTIES implements SettingsEnum {
		FTP_HOST, FTP_SSH_KEY, FTP_USER, TARGET_RELATIVE_PATH
	}

	@Override
	protected void createFieldEditors() {
		addTField(FIELD_TYPE.STRING, PROPERTIES.FTP_HOST, "ftp host");
		addTField(FIELD_TYPE.STRING, PROPERTIES.FTP_USER, "ftp user");
		addTField(FIELD_TYPE.FILE, PROPERTIES.FTP_SSH_KEY, "ftp ssh key path");
		addTField(FIELD_TYPE.STRING, PROPERTIES.TARGET_RELATIVE_PATH, "target path");

	}

	@Override
	public PROPERTIES[] getRequiredValues() {
		return PROPERTIES.values();
	}

}
