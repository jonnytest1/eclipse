package socialdear.util.dialog;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import socialdear.listeners.ImplementedEclipseMouseListener;

public class FilePickerDialogComponent {

	private String path;

	public FilePickerDialogComponent(Composite parent, String string) {

		path = string;
		addPathLAbel(parent);
		Text line = addPathLine(parent);
		addPathPickerButton(parent, line);
	}

	private void addPathLAbel(Composite dialog) {
		Label label = new Label(dialog, SWT.NULL);
		label.setText("Installation Directory:  ");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.BEGINNING;
		label.setLayoutData(gridData);
	}

	private Text addPathLine(Composite dialog) {
		Text text = new Text(dialog, SWT.BORDER | SWT.SEPARATOR_FILL | SWT.RESIZE | SWT.SPACE);
		text.setText(path);
		GridData gridDataPath = new GridData(GridData.FILL_HORIZONTAL);
		gridDataPath.grabExcessHorizontalSpace = true;
		gridDataPath.horizontalAlignment = GridData.FILL_HORIZONTAL;
		gridDataPath.minimumWidth = 200;
		gridDataPath.widthHint = 500;
		text.setLayoutData(gridDataPath);

		return text;
	}

	private void addPathPickerButton(Composite dialog, Text line) {
		Button button = new Button(dialog, SWT.PUSH);
		button.setText("...");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;

		button.addMouseListener(new ImplementedEclipseMouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				String returnPath = new DirectoryDialog(dialog.getShell()).open();
				if (returnPath != null) {
					if (!new File(returnPath).canWrite()) {
						line.setText("cannot access path");
						return;
					}
					line.setText(returnPath);
					path = returnPath;
				}
			}

		});
		button.setLayoutData(gridData);
	}

	public String getPath() {
		return path;
	}
}
