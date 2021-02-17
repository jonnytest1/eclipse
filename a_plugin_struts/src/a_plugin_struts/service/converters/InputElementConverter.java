package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;
import socialdear.logging.SystemProperties;

public class InputElementConverter extends Converter {

	private static final String G_PROPERTY = "g:property";
	private static final String VALUE = "value";

	public InputElementConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		NamedNodeMap attributes = child.getAttributes();
		String type = attributes.getNamedItem("type").getNodeValue();
		switch (type) {
			case "file":
				addElement(htmlParent, "bas-file-upload", child);
				break;
			case "checkbox":
				addCheckBoxElement(htmlParent, child);
				break;
			case "hidden":
				addHiddenElement(htmlParent, child, attributes);
				break;
			default:
				SystemProperties.print("unhandled xml " + type);
				break;
		}

	}

	private void addCheckBoxElement(Node htmlParent, Node child) {
		Element addElement = addElement(htmlParent, "bas-checkbox", child);
		if (child.getAttributes().getNamedItem(G_PROPERTY) != null) {
			String key = child.getAttributes().getNamedItem(G_PROPERTY).getNodeValue();
			getConverterAttributes().getTags().add(NG_MODEL);
			addElement.setAttribute(binding(event(NG_MODEL)), dataBinding(key));
		}
		if (getAttribute(child, VALUE) != null) {
			// String key = getAttribute(child, "value");
			// addElement.setAttribute(binding("checked"), dataBinding(key));
		}
		for (int i = 0; i < child.getChildNodes().getLength(); i++) {
			Node item = child.getChildNodes().item(i);
			if (item.getNodeName().equals("text")) {
				String childTextKEy = item.getAttributes().getNamedItem(G_CONTENT_KEY).getNodeValue();
				addElement.setAttribute(binding("description"), jsMsgKey(childTextKEy));
				break;
			}
		}
	}

	private void addHiddenElement(Node htmlParent, Node child, NamedNodeMap attributes) {
		Element input = addElement(htmlParent, "input", child);
		Node nameItem = attributes.getNamedItem("name");
		if (nameItem != null) {
			input.setAttribute("name", nameItem.getNodeValue());
		}
		Node propItem = attributes.getNamedItem(G_PROPERTY);
		if (propItem != null) {
			input.setAttribute("name", propItem.getNodeValue());
		}

		input.setAttribute("type", "hidden");

		Node idItem = attributes.getNamedItem("id");
		if (idItem != null) {
			input.setAttribute("id", idItem.getNodeValue());
		}
		if (child.getAttributes().getNamedItem(G_PROPERTY) != null) {
			String key = child.getAttributes().getNamedItem(G_PROPERTY).getNodeValue();
			getConverterAttributes().getTags().add(NG_MODEL);
			input.setAttribute(binding(event(NG_MODEL)), dataBinding(key));
		}
		Node valueNode = attributes.getNamedItem(VALUE);
		if (valueNode != null) {
			List<String> parseAttributes = parseAttributes(valueNode.getNodeValue());
			if (parseAttributes.isEmpty()) {
				input.setAttribute(binding(VALUE), valueNode.getNodeValue());
			} else {
				input.setAttribute(binding(VALUE), dataBinding(parseAttributes.get(0)));
			}
		}
	}

	@Override
	public String getSelecter() {
		return "in:input";
	}

}
