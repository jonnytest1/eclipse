package a_plugin_struts.service.converters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import a_plugin_struts.service.AngularGenerator;
import a_plugin_struts.service.ConverterAttributes;
import a_plugin_struts.service.ConverterException;
import a_plugin_struts.service.XmlHtmlConverter;
import socialdear.logging.SystemProperties;

public class IncludeConverter extends Converter {

	public IncludeConverter(XmlHtmlConverter xmlHtmlConverter) {
		super(xmlHtmlConverter);
	}

	@Override
	public void convert(Node xmlParent, Node htmlParent, Node child) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		NamedNodeMap attributes = child.getAttributes();
		Node namedItem = attributes.getNamedItem("resource");

		if (namedItem == null) {

			List<String> expression = parseAttributes(getAttribute(child, "expr"));

			if (expression.size() != 1) {
				throw new ConverterException("ecpression wrong size");
			}

			getConverterAttributes().getRestAttributes().add(expression.get(0));

			Element container = addElement(htmlParent, "div", child);
			container.setAttribute(binding("innerHTML"), dataBinding(expression.get(0)));
			SystemProperties.print("check expresssioninclude in " + getConverterAttributes().getComponentHTMLFile().getAbsolutePath());
			return;
		}

		String resource = namedItem.getNodeValue();

		List<File> filesList = getConverterAttributes().getXmlFiles().parallelStream() //
				.filter(file -> file.getAbsolutePath().endsWith(resource.replace("/", "\\"))) //
				.collect(Collectors.toList());

		if (filesList.isEmpty()) {
			throw new ConverterException("didnt find files for " + resource);
		}

		ConverterAttributes converterAtts = new ConverterAttributes(getConverterAttributes());
		converterAtts.setRelativeTo(getConverterAttributes().getAngularBaseNameAsFile());
		new AngularGenerator(null, converterAtts).generateAngularFilesForXml(filesList, false);

		getConverterAttributes().assign(converterAtts);
		String selector = converterAtts.getSelector();

		if (selector != null) {
			Element include = addElement(htmlParent, selector, child);
			include.setAttribute(binding("data"), "data");
		}

	}

	@Override
	public List<String> getSelecters() {
		return List.of("g:include");
	}

}
