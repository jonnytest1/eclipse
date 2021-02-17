package a_plugin_struts.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.swt.widgets.Display;
import org.xml.sax.SAXException;

import a_plugin_struts.model.ActionBeanObject;
import a_plugin_struts.model.FilesContainer;
import socialdear.logging.SystemProperties;

public class StrutsConverter {

	//TODO add to ts breacrumb
	private String navbarkey;

	private ActionBeanObject actionobj;
	private FilesContainer files;
	private Map<String, File> requireMap = new HashMap<>();

	private ConverterConfigDialog converterConfigDialog;

	private ConverterAttributes converterAttributes;

	public StrutsConverter(ActionBeanObject actionobj, FilesContainer files2) {
		this.actionobj = actionobj;
		this.files = files2;

	}

	public void convert() {
		Display.getDefault().asyncExec(() -> {
			converterConfigDialog = new ConverterConfigDialog(null);
			if (converterConfigDialog.open() == 1) {
				return;
			}
			converterAttributes = new ConverterAttributes(converterConfigDialog, files);
			convertFile(actionobj.getPageFiles());

			new StrutsRegistration(actionobj, converterAttributes).registrer();
		});

	}

	private void convertFile(List<File> from) {
		try {
			new AngularGenerator(actionobj, converterAttributes).generateAngularFilesForXml(from, true);
			new StrutsFiles(actionobj, converterAttributes).generateRestResource();

		} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
			SystemProperties.print(e);
		}

	}
}
