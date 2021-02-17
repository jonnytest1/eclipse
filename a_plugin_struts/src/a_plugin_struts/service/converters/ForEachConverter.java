package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.ConverterException;
import a_plugin_struts.service.XmlHtmlConverter;

public class ForEachConverter extends Converter {

	public ForEachConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);

	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Element ngFor = addElement(htmlParent, XmlHtmlConverter.NG_CONTAINER, child);

		String condition = null;
		NamedNodeMap attributes = child.getAttributes();
		if (attributes.getNamedItem("expr") != null) {
			List<String> parseAttributes = parseAttributes(attributes.getNamedItem("expr").getNodeValue());
			if (parseAttributes.size() > 1) {
				throw new ConverterException("attributes returned more than 1 in forEach");
			}
			condition = dataBinding(parseAttributes.get(0).replace("'", "\\'"));
		} else if (attributes.getNamedItem("property") != null) {
			String property = attributes.getNamedItem("property").getNodeValue();
			xmlHtmlConverter.getConverterAttributes().getRestAttributes().add(property);
			condition = dataBinding(property);
		}

		if (attributes.getNamedItem("var-property") != null) {
			String loopVariable = getSaltString();
			condition = " let " + loopVariable + " of " + condition;
			Element conditionSetter = addElement(htmlParent, NG_CONTAINER, child);
			conditionSetter.setAttribute(NG_PREFIX + "ngIf", dataBinding(loopVariable) + "=" + loopVariable);
		} else {
			condition = " let item" + getSaltString() + " of " + condition;
		}

		if (attributes.getNamedItem("pos-property") != null) {
			String indexVariable = getSaltString();
			condition += " ;let " + indexVariable + " = index";
			Element conditionSetter = addElement(htmlParent, NG_CONTAINER, child);
			conditionSetter.setAttribute(NG_PREFIX + "ngIf", dataBinding(indexVariable) + "=" + indexVariable);
		}

		ngFor.setAttribute(NG_PREFIX + "ngFor", condition);
		xmlHtmlConverter.convertNodes(child, ngFor);

	}

	@Override
	public String getSelecter() {
		return "g:for-each";
	}

}
