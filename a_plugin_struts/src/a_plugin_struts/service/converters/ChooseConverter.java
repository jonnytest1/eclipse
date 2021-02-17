package a_plugin_struts.service.converters;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import a_plugin_struts.service.XmlHtmlConverter;

public class ChooseConverter extends Converter {

	public ChooseConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		Element choose = addElement(htmlParent, NG_CONTAINER, child);
		NodeList conditionChildren = child.getChildNodes();
		String chooseConditionStr = getSaltString();
		int counter = 0;
		for (int childI = 0; childI < conditionChildren.getLength(); childI++) {
			Node conditionChild = conditionChildren.item(childI);
			if (conditionChild.getNodeName().equals("#text")) {
				continue;
			}
			Element container;
			if (counter == 0) {
				container = addElement(choose, NG_CONTAINER, null);
			} else {
				Element templateWrap = addElement(choose, "ng-template", null);
				templateWrap.setAttribute(NG_HASH + "condition" + counter + "__" + chooseConditionStr, EMPTY_DIRECTIVE);
				container = addElement(templateWrap, NG_CONTAINER, null);
			}
			Node testCondition = conditionChild.getAttributes().getNamedItem("test");

			if (testCondition != null) {
				String nodeValue = testCondition.getNodeValue();
				List<String> conditionParts = parseAttributes(nodeValue);

				for (String att : conditionParts) {
					nodeValue = nodeValue.replace(att, att.replace("'", "\\'"));
				}
				String condition = nodeValue.replace("=", "==").replace(" and ", " && ").replace(" or ", " || ");
				if (childI < conditionChildren.getLength() - 2) {
					condition += ";else condition" + (counter + 1) + "__" + chooseConditionStr;
				}
				container.setAttribute(NG_PREFIX + "ngIf", condition);
			}
			convertChildren(conditionChild, container);
			counter++;
		}

	}

	@Override
	public List<String> getSelecters() {
		return List.of("g:choose", "g:switch");
	}

}
