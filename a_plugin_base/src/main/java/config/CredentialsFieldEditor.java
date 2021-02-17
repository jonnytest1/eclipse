package config;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import socialdear.listeners.ImplementedEclipseMouseListener;

public class CredentialsFieldEditor extends FieldEditor {

	public interface ICredentialConverter {
		String convert(String username, String password) throws CredentialsException;
	}

	private ICredentialConverter converter;
	private String settingsName;

	public CredentialsFieldEditor(String name, Composite composite2) {
		super(name, "test", composite2);
		this.settingsName = name;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		//
	}

	@Override
	protected void doFillIntoGrid(Composite composite, int arg1) {
		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = HORIZONTAL_GAP;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 20;
		gridData.minimumWidth = 200;

		gridData.horizontalAlignment = GridData.FILL;

		Text username = new Text(composite, SWT.SINGLE | SWT.BORDER);
		username.setLayoutData(gridData);
		username.setEditable(true);
		username.setText("username");

		username.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// nothin

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (username.getText().equals("username")) {
					username.setText("");
				}

			}
		});
		username.setSize(new Point(50, 50));

		Text password = new Text(composite, SWT.SINGLE | SWT.BORDER);
		password.setEditable(true);
		password.setLayoutData(gridData);
		password.setText("password");
		password.setRedraw(true);
		password.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// nothin

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (password.getText().equals("password")) {
					password.setText("");
				}

			}
		});

		Button button = new Button(composite, SWT.PUSH);
		button.setText("request Token");
		button.addMouseListener(new ImplementedEclipseMouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				try {
					String credentials = converter.convert(username.getText(), password.getText());
					if (credentials != null) {
						getPreferenceStore().setValue(settingsName, credentials);
					}
				} catch (CredentialsException e) {
					CredentialsFieldEditor.this.showErrorMessage(e.getText());

				}

			}

		});

		composite.pack();
		composite.update();
	}

	@Override
	protected void doLoad() {
		// empty
	}

	@Override
	protected void doLoadDefault() {
		// empty
	}

	@Override
	protected void doStore() {
		// empty

	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	public void setConverter(ICredentialConverter converer) {
		this.converter = converer;

	}

}
