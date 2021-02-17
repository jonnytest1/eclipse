package views.migration;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {
	public static final String ID = "testPlugin.view";

	@Inject
	IWorkbench workbench;

	private Composite parent;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED);
		new MigrationLogic(swtAwtComponent);
	}

	@Override
	public void setFocus() {
		parent.setFocus();
	}

}