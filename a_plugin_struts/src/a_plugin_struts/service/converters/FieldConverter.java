package a_plugin_struts.service.converters;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;
import socialdear.logging.SystemProperties;

public class FieldConverter extends Converter {

	private static final String BAS_LABEL = "bas-label";

	public FieldConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Node typeNode = child.getAttributes().getNamedItem("type");
		String fieldSelector = "bas-field";
		if (typeNode != null) {
			String type = typeNode.getNodeValue();
			if (type.equals("buttonline")) {
				//handled above
				fieldSelector = "bas-button-line";
			}
		}

		Element fieldElement = addElement(htmlParent, fieldSelector, child);
		NodeList children = child.getChildNodes();

		setType(typeNode, fieldElement);

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("text")) {
				addElement(fieldElement, NG_CONTAINER, children.item(i));
				break;
			}
		}

		boolean handled = false;
		for (int i = 0; i < children.getLength(); i++) {
			handled = handleSubElement(fieldElement, children, i);
			if (handled) {
				break;
			}
		}

		if (!handled) {
			// convertChildren(child, fieldElement);
		}

	}

	private void setType(Node typeNode, Element fieldElement) {
		if (typeNode != null) {
			String type = typeNode.getNodeValue();
			if (type.equals("no-label-2-4")) {
				fieldElement.setAttribute(binding("class"), "'cols2'");
			} else if (type.equals("no-label")) {
				// nothin
			} else if (type.equals("buttonline")) {
				// handled above
			} else if (type.equals("1-4")) {
				// TODO
			} else {
				SystemProperties.print("unknown type " + type);
			}
		}
	}

	private boolean handleSubElement(Element fieldElement, NodeList children, int i) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Node fieldChild = children.item(i);
		if (fieldChild.getNodeName().equals("in:submit")) {
			Element label = addElement(fieldElement, LABEL, null);
			label.setAttribute(BAS_LABEL, EMPTY_DIRECTIVE);
			xmlHtmlConverter.addSubmit(label, fieldChild);
			return true;
		} else if (fieldChild.getNodeName().equals("#text")) {
			//nothing
		} else if (fieldChild.getNodeName().equals("label") || fieldChild.getNodeName().equals("text")) {
			Element label = addElement(fieldElement, LABEL, fieldChild);
			label.setAttribute(BAS_LABEL, EMPTY_DIRECTIVE);
			return true;
		} else {
			SystemProperties.print("field implementation may be necessary for " + fieldChild.getNodeName());
			xmlHtmlConverter.convertNodes(fieldChild, fieldElement);
		}
		return false;
	}

	@Override
	public String getSelecter() {
		return "ly:field";
	}

}
