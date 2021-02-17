package a_plugin_struts.service.converters;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.StrutsProperties.STRUTS_SETTINGS;
import a_plugin_struts.model.PositionalXMLReader;
import a_plugin_struts.service.XmlHtmlConverter;

public class XSLConverter extends Converter {

	private File mainFile;

	private Map<String, String> templates;

	public XSLConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
		String xslFile = StrutsProperties.getValue(STRUTS_SETTINGS.XSL_File);
		this.mainFile = new File(xslFile);
	}

	private void parseXslFile() throws IOException, SAXException {
		Document document = PositionalXMLReader.readXML(mainFile);
		document.getFirstChild();

	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) {

	}

	@Override
	public String getSelecter() {
		return null;
	}

}
