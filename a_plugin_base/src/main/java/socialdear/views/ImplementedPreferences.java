package socialdear.views;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import config.CredentialsFieldEditor;
import socialdear.exceptions.MissingImplementationException;
import socialdear.logging.SystemProperties;

public abstract class ImplementedPreferences<T extends Enum<T> & SettingsEnum> extends ImplementedFieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected enum FIELD_TYPE {
		STRING, COMBO, FILE, CHECKBOX, CREDENTIAL,
		/**
		 * @since 3.0
		 */
		NUMBER
	}

	private Map<T, String> hintMap = new HashMap<>();

	private static Map<Class<?>, Boolean> hintCheck = new HashMap<>();

	private static Map<String, ScopedPreferenceStore> stores = new HashMap<>();

	private boolean testMode = false;

	public ImplementedPreferences() {
		if (hintCheck.get(this.getClass()) == null) {
			hintCheck.put(this.getClass(), true);
			testMode = true;
			createFieldEditors();
			testMode = false;
		}

	}

	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(stores.put(getStoreDescriptor(), new ScopedPreferenceStore(InstanceScope.INSTANCE, getStoreDescriptor())));

	}

	public String getStoreDescriptor() {
		return this.getClass().getCanonicalName();
	}

	protected Optional<FieldEditor> addTField(FIELD_TYPE type, T setting, String hint, Object... option) {

		setDefault(setting);

		String name = setting.name();
		hintMap.put(setting, hint);
		if (testMode) {
			return Optional.empty();
		}

		FieldEditor editor = null;
		switch (type) {
			case STRING:
				editor = new StringFieldEditor(name, hint, getFieldEditorParent());
				addField(editor);
				break;
			case COMBO:
				editor = new ComboFieldEditor(name, hint, (String[][]) option[0], getFieldEditorParent());
				addField(editor);
				break;
			case FILE:
				editor = new FileFieldEditor(name, hint, true, getFieldEditorParent());
				addField(editor);
				break;
			case CHECKBOX:
				editor = new BooleanFieldEditor(name, hint, getFieldEditorParent());
				addField(editor);
				break;
			case CREDENTIAL:
				editor = new CredentialsFieldEditor(name, getFieldEditorParent());
				addField(editor);
				break;
			case NUMBER:
				editor = new IntegerFieldEditor(name, hint, getFieldEditorParent());
				addField(editor);
				break;
			default:
				break;
		}
		return Optional.of(editor);

	}

	private void setDefault(T setting) {
		Object defaultValue = setting.getDefault();
		if (defaultValue == null) {
			return;
		}
		String name = setting.name();

		if (getPreferenceStore() == null) {
			ScopedPreferenceStore scopedPreferenceStore = stores.computeIfAbsent(getStoreDescriptor(), key -> new ScopedPreferenceStore(InstanceScope.INSTANCE, key));
			setPreferenceStore(scopedPreferenceStore);
		}

		if (defaultValue instanceof String) {
			getPreferenceStore().setDefault(name, (String) defaultValue);
		} else if (defaultValue instanceof Boolean) {
			getPreferenceStore().setDefault(name, (Boolean) defaultValue);
		} else if (defaultValue instanceof Integer) {
			getPreferenceStore().setDefault(name, (Integer) defaultValue);
		} else {
			throw new MissingImplementationException();
		}

	}

	@Override
	protected void addField(FieldEditor editor) {
		if (!testMode) {
			super.addField(editor);
		}
	}

	public abstract T[] getRequiredValues();

	/**
	 * @since 3.0
	 */
	public static <E extends Enum<E> & SettingsEnum> Integer getIntegerValue(E id) {
		try {
			return getPreferenceInstance(id).getInt(id.name());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
			return null;
		}
	}

	public static <E extends Enum<E> & SettingsEnum> String getValue(E id) {
		try {
			return getPreferenceInstance(id).getString(id.name());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
			return null;
		}
	}

	public static <E extends Enum<E> & SettingsEnum> boolean getBooleanValue(E id) {
		try {
			return getPreferenceInstance(id).getBoolean(id.name());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
			return false;
		}
	}

	public static <E extends Enum<E> & SettingsEnum> void setValue(E id, String value) {
		try {
			getPreferenceInstance(id).setValue(id.name(), value);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
		}
	}

	private static <E extends Enum<E> & SettingsEnum> ScopedPreferenceStore getPreferenceInstance(E id) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Class<?> settingsClass = id.getClass().getEnclosingClass();
		if (settingsClass.toString().contains("$")) {
			settingsClass = settingsClass.getEnclosingClass();
		}

		@SuppressWarnings("unchecked") ImplementedPreferences<E> instance = (ImplementedPreferences<E>) settingsClass.getConstructor().newInstance();
		return stores.computeIfAbsent(instance.getStoreDescriptor(), key -> new ScopedPreferenceStore(InstanceScope.INSTANCE, key));

	}

	@Override
	public String checkPreferences() {
		boolean show = false;
		errorMessage = new StringBuilder("following settigns are missing or invalid:\n");

		for (T requiredValue : getRequiredValues()) {
			if ("".equals(getValue(requiredValue))) {
				show = true;
				String hint = hintMap.get(requiredValue);
				if (hint == null) {
					hint = requiredValue.name();
				}
				errorMessage.append(hint + "\n");
			}
		}
		if (show) {
			return getStoreDescriptor();
		}

		return null;
	}
}
