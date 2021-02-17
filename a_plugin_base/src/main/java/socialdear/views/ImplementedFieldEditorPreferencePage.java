package socialdear.views;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class ImplementedFieldEditorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	protected StringBuilder errorMessage = new StringBuilder();

	protected ImplementedFieldEditorPreferencePage() {
		super(GRID);
	}

	public abstract String checkPreferences();

	@Override
	public String getErrorMessage() {
		String superError = super.getErrorMessage();
		if (errorMessage.length() == 0 && superError != null) {
			return superError;
		}

		String string = errorMessage.toString();
		if (string.isEmpty()) {
			return null;
		}
		return string;
	}

}
