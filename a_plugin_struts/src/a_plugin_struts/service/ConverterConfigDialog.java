package a_plugin_struts.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.StrutsProperties.STRUTS_SETTINGS;
import socialdear.listeners.ImplementedEclipseMouseListener;;

public class ConverterConfigDialog extends TitleAreaDialog {

	private Text webservicePathTExt;

	private Text angularPathText;
	private String angularPath;
	
	private Map<Text,STRUTS_SETTINGS> values=new HashMap<>();


	public ConverterConfigDialog(Shell parentShell) {
		super(parentShell);

	}

	@Override
	public void create() {
		super.create();
		setTitle("Parse XML File to Angular component");
		setMessage("please check git commit for added changes", IMessageProvider.INFORMATION);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		
		addPicker(container, "Webservice",STRUTS_SETTINGS.REST,false);
		createAngularLocation(container);
		addPicker(container, "rest REgistry",STRUTS_SETTINGS.REGISTRY,true);
		addPicker(container, "angular module",STRUTS_SETTINGS.ANGULAR_MODULE,true);
		Label routeLabel = addPicker(container, "angular route",STRUTS_SETTINGS.ANGULAR_ROUTE,true);
		routeLabel.setToolTipText("can be in angular module ness to start with 'const routes: Routes = ['");
		addCustomerList(container);
		
		return area;
	}

	private void addCustomerList(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Kunden");
		lbtLastName.setToolTipText("Komma getrennte Liste von kunden für pattern matching");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		Text customerlist = new Text(container, SWT.BORDER);
		customerlist.setLayoutData(dataLastName);
		customerlist.setText(	StrutsProperties.getValue(StrutsProperties.STRUTS_SETTINGS.CUSTOMERS));
		values.put(customerlist, STRUTS_SETTINGS.CUSTOMERS);
	}

	private void createAngularLocation(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Angular folder");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		angularPathText = new Text(container, SWT.BORDER);
		angularPathText.setLayoutData(dataLastName);
		angularPathText.setText(	StrutsProperties.getValue(StrutsProperties.STRUTS_SETTINGS.ANGULAR));
		
		Button button = new Button(container, SWT.NONE);
		button.setText("pick");
		button.addMouseListener(new ImplementedEclipseMouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				DirectoryDialog fileDialog = new DirectoryDialog(getShell());
				String directory = fileDialog.open();
				angularPathText.setText(directory);
			}
		});

	}
	
	private Label addPicker(Composite container,String name,STRUTS_SETTINGS key,boolean file) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText(name);
		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		Text restRegistry = new Text(container, SWT.BORDER);
		restRegistry.setLayoutData(dataLastName);
		restRegistry.setText(	StrutsProperties.getValue(key));
		Button button = new Button(container, SWT.NONE);
		button.setText("pick");
		button.addMouseListener(new ImplementedEclipseMouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				if(file) {
					FileDialog fileDialog = new FileDialog(getShell());
					String fileStr = fileDialog.open();
					restRegistry.setText(fileStr);
					StrutsProperties.setValue(key,fileStr);
				}else {
					DirectoryDialog fileDialog = new DirectoryDialog(getShell());
					String fileStr = fileDialog.open();
					restRegistry.setText(fileStr);
					StrutsProperties.setValue(key,fileStr);
				}
	
			}
		});
		
		values.put(restRegistry, key);
		return lbtLastName;
	}
	

	private void saveInput() {
		angularPath = angularPathText.getText();
		StrutsProperties.setValue(StrutsProperties.STRUTS_SETTINGS.ANGULAR,angularPath);				
		values.forEach((text,key)->{
			String text2 = text.getText();
			if(text2==null||text2.isBlank()) {
				throw new RuntimeException("missing text");
			}
			StrutsProperties.setValue(key,text2);
		});
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getWebservicePath() {
		return StrutsProperties.getValue(StrutsProperties.STRUTS_SETTINGS.REST);
	}

	public File getAngularPath() {
		return new File(angularPath);
	}


}
