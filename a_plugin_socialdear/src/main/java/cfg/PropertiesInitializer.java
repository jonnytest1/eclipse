package cfg;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PropertiesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				SystemSettings.PROPERTY_STORE_DESCRIPTOR);
		scopedPreferenceStore.setDefault(SystemSettings.MARTHON_GROUP_FILTER, "/test/socialdear");
		scopedPreferenceStore.setDefault(SystemSettings.LOCALMYHOST, System.getProperty("user.name"));
	}

}
