package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;

public class SkipElementConverter extends Converter {

	public SkipElementConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		convertChildren(child, htmlParent);
	}

	@Override
	public List<String> getSelecters() {
		return List.of("g:fragment", "page");
	}

}
