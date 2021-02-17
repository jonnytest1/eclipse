package a_plugin_struts.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import a_plugin_struts.model.PositionalXMLReader;
import a_plugin_struts.service.converters.ChooseConverter;
import a_plugin_struts.service.converters.ConditionConverter;
import a_plugin_struts.service.converters.Converter;
import a_plugin_struts.service.converters.FieldConverter;
import a_plugin_struts.service.converters.ForEachConverter;
import a_plugin_struts.service.converters.IncludeConverter;
import a_plugin_struts.service.converters.InputElementConverter;
import a_plugin_struts.service.converters.SimpleElementConverter;
import a_plugin_struts.service.converters.SkipElementConverter;
import a_plugin_struts.service.converters.XSLConverter;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;

public class XmlHtmlConverter {

	private static final String STYLE = "style";

	public static final String NG_CONTAINER = "ng-container";

	private static final String NG_BIND_0 = "_ngbind0_";
	private static final String NG_BIND_1 = "_ngbind1_";
	private static final String NG_EVENT_0 = "_ngevent0_";
	private static final String NG_EVENT_1 = "_ngevent1_";
	private static final String EMPTY_DIRECTIVE = "_ng_directive_";
	private static final String NG_HASH = "_ng_hash_";
	private static final String NG_PREFIX = "_ngprefix_";

	enum REPLACEMENTS {

		NGPEFIX(NG_PREFIX, "*"), NG_BIND_0(XmlHtmlConverter.NG_BIND_0, "["), NG_BIND_1(XmlHtmlConverter.NG_BIND_1, "]"), //
		NG_EVENT_0(XmlHtmlConverter.NG_EVENT_0, "("), NG_EVENT_1(XmlHtmlConverter.NG_EVENT_1, ")"), //
		NG_DIRECTIVE("=\"" + EMPTY_DIRECTIVE + "\"", ""), NG_ID(XmlHtmlConverter.NG_HASH, "#");

		private String xml;
		private String replacement;

		REPLACEMENTS(String xml, String replacement) {
			this.xml = xml;
			this.replacement = replacement;

		}

		public String getXml() {
			return xml;
		}

		public String getReplacement() {
			return replacement;
		}
	}

	private static final String G_ALT_KEY = "g:alt-key";
	private static final String G_CONTENT_KEY = "g:content-key";
	private static final String G_ACTION_PAGE = "g:action-page";

	private static final String ACTION = "action";

	private Document htmlDocument;
	private ConverterAttributes converterAttributes;

	private List<Converter> converters;

	XmlHtmlConverter(ConverterAttributes converterAttributes) throws ParserConfigurationException {
		this.converterAttributes = converterAttributes;
		converters = List.of(//
				new XSLConverter(this), //
				new ForEachConverter(this), //
				new SimpleElementConverter(this), //
				new IncludeConverter(this), //
				new SkipElementConverter(this), //
				new InputElementConverter(this), //
				new ChooseConverter(this), //
				new ConditionConverter(this), //
				new FieldConverter(this));
	}

	public void convert() throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Document document = PositionalXMLReader.readXML(converterAttributes.getConvertingFile());
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		htmlDocument = docBuilder.newDocument();

		Element html = htmlDocument.createElement("html");
		htmlDocument.appendChild(html);

		convertChildren(document, html);

		DOMSource domSource = new DOMSource(htmlDocument);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);

		String htmlString = writer.toString();
		for (REPLACEMENTS r : REPLACEMENTS.values()) {
			htmlString = htmlString.replace(r.getXml(), r.getReplacement());
		}
		htmlString = htmlString.replace("<html>\r\n", "").replace("</html>", "").replace("&amp;", "&");
		htmlString += "<!-- converted from " + converterAttributes.getConvertingFile().getAbsolutePath() + " -->";

		FileParser.writeFile(converterAttributes.getComponentHTMLFile(), htmlString);
	}

	public void convertChildren(Node parent, Node htmlParent) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			convertNodes(child, htmlParent);
		}
	}

	public void convertNodes(Node child, Node htmlParent) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		for (Converter converter : converters) {
			if (converter.getSelecters().contains(child.getNodeName()) || child.getNodeName().equals(converter.getSelecter())) {
				converter.convert(child.getParentNode(), htmlParent, child);
				return;
			}
		}

		try {
			NamedNodeMap attributes = child.getAttributes();
			switch (child.getNodeName()) {
				case "g:add-to-navbar":
					addElement(htmlParent, "bas-breadcrumb", child);
					Node descKey = attributes.getNamedItem("description-key");
					if (descKey != null) {
						String text = null;
						if (descKey.getNodeValue().startsWith("{")) {
							List<String> keys = parseAttributes(descKey.getNodeValue());
							if (keys.size() != 1) {
								throw new ConverterException("multiple attributes ");
							}
							text = bindingHtmlMsgKey(keys.get(0));
						} else {
							text = htmlMsgKey(descKey.getNodeValue());
						}

						Element h1 = addElement(htmlParent, "h1", child);
						h1.setTextContent(text);
					}
					break;
				case "a":
					Element link = addElement(htmlParent, "a", child);
					String nodeValue = child.getAttributes().getNamedItem("href").getNodeValue();
					List<String> attrs = parseAttributes(nodeValue);
					if (attrs.isEmpty()) {
						throw new ConverterException("too many attributes for link");
					}
					link.setAttribute(binding("href"), dataBinding(attrs.get(0)));
					break;
				case "#text":
					if (child.getTextContent().trim().isBlank()) {
						break;
					}
					break;
				case "ly:layout":
					addHeadline(child, htmlParent);
					convertChildren(child, htmlParent);
					break;
				case "in:form":
					Element foir = addElement(htmlParent, "form", child);
					foir.setAttribute(NG_PREFIX + "ngIf", "data");

					if (attributes.getNamedItem(G_ACTION_PAGE) != null) {
						converterAttributes.setFormActionString("'" + attributes.getNamedItem(G_ACTION_PAGE).getNodeValue() + "'");
					} else if (attributes.getNamedItem(ACTION) != null) {
						List<String> atts = parseAttributes(attributes.getNamedItem(G_ACTION_PAGE).getNodeValue());
						converterAttributes.setFormActionString("this." + dataBinding(atts.get(0)));
					}
					foir.setAttribute("method", "POST");
					convertChildren(child, foir);
					break;
				case "text":
					addText(child, htmlParent);
					break;
				case "script":
					Element script = addElement(htmlParent, "script", child);
					script.setTextContent(child.getTextContent());
					break;
				case "g:execute":
				case "g:executefirst":
					converterAttributes.addJavaExecute(child.getTextContent().replace("'", "\""));
					break;
				case "in:submit":
					// submit without field
					addSubmit(htmlParent, child);
					break;
				case "img":
				case "g:import":
				case "g:errors":
				case "g:message":
				case "ly:overviewArea":
				case "ly:dialog":
				case "ly:inputseparator":
				case "ly:overviewButtons":
				case "ly:webtmModeDecorator":
				case "ly:legend":
				case "ly:tabulation":
					SystemProperties.print("missing implementation for " + child.getNodeName());
					break;
				default:
					SystemProperties.print("unhandled xml " + child.getNodeName());
					throw new ConverterException("missing xml " + child.getNodeName() + " in " + converterAttributes.getConvertingFile().getAbsolutePath());
			}
		} catch (Exception e) {
			throw new ConverterException("Exeption in child " + child.getNodeName() + converterAttributes.getConvertingFile().getAbsolutePath(), e);
		}
	}

	public void addSubmit(Node htmlParent, Node child) {
		Element sumitLink = addElement(htmlParent, "a", child);
		sumitLink.setAttribute("class", "formSubmit button");

		Node codeAttribute = child.getAttributes().getNamedItem("code");
		if (codeAttribute != null) {
			sumitLink.setAttribute(NG_EVENT_0 + "click" + NG_EVENT_1, "submit('" + codeAttribute.getNodeValue() + "',$event)");
		}

		Node inactive = child.getAttributes().getNamedItem("inactive");
		if (inactive != null) {
			sumitLink.setAttribute("disabled", inactive.getNodeValue());
		}
	}

	private List<String> parseAttributes(String dataAttribute) {
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
						converterAttributes.getRestAttributes().add(condition);
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

	private void addHeadline(Node child, Node htmlParent) {
		//String navbarkey = child.getAttributes().getNamedItem(G_ALT_KEY).getNodeValue();
		//converterAttributes.setNavbarKey(navbarkey);
		Element headline = addElement(htmlParent, "h1", child);
		headline.setAttribute("bas-headline", EMPTY_DIRECTIVE);
	}

	private Element addElement(Node htmlParent, String tag, Node child) {
		converterAttributes.getTags().add(tag);
		Element element = htmlDocument.createElement(tag);
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
				converterAttributes.getRestAttributes().add(key);
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

			if (child.getAttributes().getNamedItem("class") != null) {
				String key = child.getAttributes().getNamedItem("class").getNodeValue();
				element.setAttribute("class", key);
			}
		}

		return element;
	}

	private void addText(Node child, Node htmlParent) {
		Element divPArent = addElement(htmlParent, "div", child);
		Node namedItem = child.getAttributes().getNamedItem(G_CONTENT_KEY);
		if (namedItem == null && !child.getTextContent().isBlank()) {
			divPArent.setTextContent(child.getTextContent());
		}
	}

	private String dataBinding(String string) {
		return "data['" + string.replace("'", "\\'") + "']";
	}

	private String binding(String name) {
		return NG_BIND_0 + name + NG_BIND_1;
	}

	private String jsMsgKey(String key) {
		return "'" + key + "' | msgKey";
	}

	private String bindingMsgKey(String key) {
		return "data['" + key + "'] | msgKey";
	}

	private String bindingHtmlMsgKey(String key) {
		return "{{ " + bindingMsgKey(key) + " }}";
	}

	private String htmlMsgKey(String key) {
		return "{{ " + jsMsgKey(key) + " }}";
	}

	public ConverterAttributes getConverterAttributes() {
		return converterAttributes;
	}

	public Document getHtmlDocument() {
		return htmlDocument;
	}
}
