package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;

public class SimpleElementConverter extends Converter {

	public SimpleElementConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		String nodeName = child.getNodeName();
		if (nodeName.equals("ly:item")) {
			nodeName = "div";
		} else if (nodeName.equals("ly:group")) {
			nodeName = "bas-group";
		}

		convertChildren(child, addElement(htmlParent, nodeName, child));
	}

	@Override
	public List<String> getSelecters() {
		return List.of("div", "br", "label", "ly:item", "ly:group");
	}

}
