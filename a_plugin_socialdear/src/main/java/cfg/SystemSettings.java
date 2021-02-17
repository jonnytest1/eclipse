package cfg;

import java.util.Base64;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import cfg.SystemSettings.SD_SETTINGS;
import socialdear.views.ImplementedPreferences;
import socialdear.views.SettingsEnum;

public class SystemSettings extends ImplementedPreferences<SD_SETTINGS> implements IWorkbenchPreferencePage {

	public enum SD_SETTINGS implements SettingsEnum {
		MARTHONPASSWORD, MARTHONUSER, MARATHONHOST, ELKHOST, MARTHON_GROUP_FILTER, LOCALMYHOST
	}

	public static final String PROPERTY_STORE_DESCRIPTOR = "socialdear.properties";

	static Long lastDisplayForce = 0L;

	public static final String MARTHON_GROUP_FILTER = "MARTHON_GROUP_FILTER";

	public static final String LOCALMYHOST = "LOCALMYHOST";

	private static ScopedPreferenceStore store;

	public SystemSettings() {
		// default
	}

	@Override
	public void init(IWorkbench arg0) {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PROPERTY_STORE_DESCRIPTOR);
		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
		addTField(FIELD_TYPE.STRING, SD_SETTINGS.MARTHONUSER, "Marathon Username");
		addTField(FIELD_TYPE.STRING, SD_SETTINGS.MARTHONPASSWORD, "Marathon Password");
		/*
		 * addField( new StringFieldEditor(SD_SETTINGS.MARTHONPASSWORD.name(),
		 * "Marathon Password", getFieldEditorParent()) {
		 * 
		 * @Override protected void doFillIntoGrid(Composite parent, int numColumns) {
		 * super.doFillIntoGrid(parent, numColumns); getTextControl().setEchoChar('*');
		 * } });
		 */
		addTField(FIELD_TYPE.STRING, SD_SETTINGS.MARTHON_GROUP_FILTER, "Marathon group filter");
		addTField(FIELD_TYPE.STRING, SD_SETTINGS.LOCALMYHOST, "localhost elk identifier");
	}

	public static final String getMarathonGroupFilter() {
		return getValue(MARTHON_GROUP_FILTER);
	}

	public static final String getMarathonGroupsUrl() {
		return getValue(SD_SETTINGS.MARATHONHOST) + "/v2/groups" + getMarathonGroupFilter();
	}

	public static final String getMarathonAppsUrl() {
		return getValue(SD_SETTINGS.MARATHONHOST) + "/v2/groups" + getMarathonGroupFilter();
	}

	public static String getMarathonHeader() {
		String password = getValue(SD_SETTINGS.MARTHONPASSWORD);
		String username = getValue(SD_SETTINGS.MARTHONUSER);

		String base64 = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		return "Basic " + base64;
	}

	private static String getValue(String id) {
		if (store == null) {
			store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PROPERTY_STORE_DESCRIPTOR);
		}
		return store.getString(id);
	}

	@Override
	public String checkPreferences() {
		boolean show = false;
		errorMessage = new StringBuilder("following settigns are missing or invalid:\n");
		if (getValue(SD_SETTINGS.MARTHONPASSWORD).equals("") || getValue(SD_SETTINGS.MARTHONUSER).equals("")) {
			show = true;
			errorMessage.append("marathon auth\n");
		}
		if (show) {
			return PROPERTY_STORE_DESCRIPTOR;
		}

		return null;
	}

	@Override
	public SD_SETTINGS[] getRequiredValues() {

		return new SD_SETTINGS[] { SD_SETTINGS.MARTHONPASSWORD, SD_SETTINGS.MARTHONUSER };
	}

}
