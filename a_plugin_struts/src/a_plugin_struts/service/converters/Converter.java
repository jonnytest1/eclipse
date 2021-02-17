package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import a_plugin_struts.service.ConverterAttributes;
import a_plugin_struts.service.ConverterException;
import a_plugin_struts.service.XmlHtmlConverter;

public abstract class Converter {

	private static final String CLASS = "class";

	protected static final String LABEL = "label";

	protected static final String MSG_KEY = "' | msgKey }}";

	private static final String G_ALT_KEY = "g:alt-key";
	protected static final String G_CONTENT_KEY = "g:content-key";
	protected static final String G_ACTION_PAGE = "g:action-page";

	protected static final String ACTION = "action";

	protected static final String NG_MODEL = "ngModel";

	private static final String STYLE = "style";

	public static final String NG_CONTAINER = "ng-container";

	private static Random rnd = new Random();

	private static final String NG_BIND_0 = "_ngbind0_";
	private static final String NG_BIND_1 = "_ngbind1_";
	private static final String NG_EVENT_0 = "_ngevent0_";
	private static final String NG_EVENT_1 = "_ngevent1_";
	protected static final String EMPTY_DIRECTIVE = "_ng_directive_";
	protected static final String NG_HASH = "_ng_hash_";
	protected static final String NG_PREFIX = "_ngprefix_";

	protected XmlHtmlConverter xmlHtmlConverter;

	Converter(XmlHtmlConverter xmlHtmlConverter) {
		this.xmlHtmlConverter = xmlHtmlConverter;

	}

	public abstract void convert(Node xmlParent, Node htmlParent, Node currentChild) throws IOException, SAXException, ParserConfigurationException, TransformerException;

	public String getSelecter() {
		return null;
	}

	public List<String> getSelecters() {
		return List.of();
	}

	protected List<String> parseAttributes(String dataAttribute) {
		ArrayList<String> attList = new ArrayList<>();
		int index = dataAttribute.indexOf("form(", -1) + 5;
		int bracketCount = 1;
		while (index > 4) {
			for (int i = index; i < dataAttribute.length(); i++) {
				char character = dataAttribute.charAt(i);
				if ("(".charAt(0) == character) {
					bracketCount++;
				}
				if (")".charAt(0) == character) {
					bracketCount--;
					if (bracketCount == 0) {
						String condition = dataAttribute.substring(index + 1, i - 1);
						xmlHtmlConverter.getConverterAttributes().getRestAttributes().add(condition);
						attList.add(condition);
						index = dataAttribute.indexOf("form(", i) + 5;
						bracketCount = 1;
						break;
					}
				}
			}
		}
		return attList;
	}

	protected Element addElement(Node htmlParent, String tag, Node child) {
		xmlHtmlConverter.getConverterAttributes().getTags().add(tag);
		Element element = xmlHtmlConverter.getHtmlDocument().createElement(tag);
		htmlParent.appendChild(element);
		if (child != null) {
			if (child.getAttributes().getNamedItem(G_ALT_KEY) != null) {
				String key = child.getAttributes().getNamedItem(G_ALT_KEY).getNodeValue();
				element.setTextContent(htmlMsgKey(key));
			}
			if (child.getAttributes().getNamedItem(G_CONTENT_KEY) != null) {
				String key = child.getAttributes().getNamedItem(G_CONTENT_KEY).getNodeValue();
				element.setTextContent(htmlMsgKey(key));
			}
			if (child.getAttributes().getNamedItem("g:content-property") != null) {
				String key = child.getAttributes().getNamedItem("g:content-property").getNodeValue();
				xmlHtmlConverter.getConverterAttributes().getRestAttributes().add(key);
				element.setTextContent("{{ " + dataBinding(key) + " }}");
			}
			if (child.getAttributes().getNamedItem("g:title-key") != null) {
				String key = child.getAttributes().getNamedItem("g:title-key").getNodeValue();
				element.setAttribute("title", htmlMsgKey(key));
			}
			if (child.getAttributes().getNamedItem("id") != null) {
				String key = child.getAttributes().getNamedItem("id").getNodeValue();
				element.setAttribute("id", key);
			}
			if (child.getAttributes().getNamedItem(STYLE) != null) {
				String key = child.getAttributes().getNamedItem(STYLE).getNodeValue();
				element.setAttribute(STYLE, key);
			}

			if (child.getAttributes().getNamedItem(CLASS) != null) {
				String key = child.getAttributes().getNamedItem(CLASS).getNodeValue();
				element.setAttribute(CLASS, key);
			}
		}

		return element;
	}

	protected String getSaltString() {
		String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < 18) { // length of the random string.
			int index = rnd.nextInt(saltChars.length() - 1);
			salt.append(saltChars.charAt(index));
		}
		return salt.toString();

	}

	protected String dataBinding(String string) {
		return "data['" + string.replace("'", "\\'") + "']";
	}

	protected String binding(String name) {
		return NG_BIND_0 + name + NG_BIND_1;
	}

	protected String event(String name) {
		return NG_EVENT_0 + name + NG_EVENT_1;
	}

	protected String jsMsgKey(String key) {
		return "'" + key + "' | msgKey";
	}

	private String bindingMsgKey(String key) {
		return "data['" + key + "'] | msgKey";
	}

	protected String bindingHtmlMsgKey(String key) {
		return "{{ " + bindingMsgKey(key) + " }}";
	}

	protected String htmlMsgKey(String key) {
		return "{{ " + jsMsgKey(key) + " }}";
	}

	protected String getAttribute(Node child, String attribute) {
		if (child.getAttributes() != null && child.getAttributes().getNamedItem(attribute) != null) {
			return child.getAttributes().getNamedItem(attribute).getNodeValue();
		}
		return null;
	}

	protected void convertChildren(Node xmlParent, Node htmlParent) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		NodeList children = xmlParent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (filterChild(child)) {
				try {
					xmlHtmlConverter.convertNodes(child, htmlParent);
				} catch (Exception e) {
					throw new ConverterException("error converting child " + child.getNodeName(), e);
				}
			}

		}
	}

	ConverterAttributes getConverterAttributes() {
		return xmlHtmlConverter.getConverterAttributes();
	}

	protected boolean filterChild(Node child) {
		child.getAttributes();
		return true;
	}
}
