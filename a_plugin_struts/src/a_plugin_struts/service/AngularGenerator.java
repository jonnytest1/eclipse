package a_plugin_struts.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import a_plugin_struts.model.ActionBeanObject;
import socialdear.util.files.FileParser;

public class AngularGenerator {

	private StrutsFiles filesGenerator;
	private ConverterAttributes converterAttributes;

	public AngularGenerator(ActionBeanObject action, ConverterAttributes converterAttributes) {
		this.converterAttributes = converterAttributes;
		filesGenerator = new StrutsFiles(action, converterAttributes);
	}

	public void generateAngularFilesForXml(List<File> xmlFiles, boolean module) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		for (File xmlFile : xmlFiles) {
			converterAttributes.setConvertingFile(xmlFile);
			new XmlHtmlConverter(converterAttributes).convert();
		}
		File htmlBase = converterAttributes.getComponentHTMLFileWithoutCustomer();
		if (!htmlBase.exists()) {
			FileParser.writeFile(htmlBase, "");
		}

		filesGenerator.generateTypescriptFile(module);
		FileParser.writeFile(converterAttributes.getLessFile(), "");
		if (module) {
			filesGenerator.generateModuleFile();
			filesGenerator.generateServiceFile();
		}

	}
}
