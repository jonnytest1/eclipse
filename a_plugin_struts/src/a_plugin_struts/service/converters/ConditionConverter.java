package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;

public class ConditionConverter extends Converter {

	public ConditionConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Element addElement = addElement(htmlParent, NG_CONTAINER, child);
		String dataAttribute = child.getAttributes().getNamedItem("test").getNodeValue();
		dataAttribute = dataAttribute.replace("or ", "|| ").replace("and ", "&& ").replace("=", "==");
		List<String> attributes = parseAttributes(dataAttribute);

		for (String att : attributes) {
			dataAttribute = dataAttribute.replace(att, att.replace("'", "\\'"));
		}

		addElement.setAttribute(NG_PREFIX + "ngIf", dataAttribute);
		convertChildren(child, addElement);

	}

	@Override
	public String getSelecter() {
		return "g:if";
	}

}
