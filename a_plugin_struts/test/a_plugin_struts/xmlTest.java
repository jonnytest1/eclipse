package a_plugin_struts;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import a_plugin_struts.model.ActionBeanObject;
import a_plugin_struts.model.PositionalXMLReader;
import a_plugin_struts.service.StrutsConverter;

public class xmlTest {

	@Test
	@Ignore
	public void xmltest() throws IOException, SAXException, TransformerException {
		Document doc = PositionalXMLReader.readXML(new File(
				"D:\\Jonathan\\eclipse-workspaces\\201907\\plugin\\PluginCreation\\a_plugin_struts\\test\\a_plugin_struts\\test.xml"));
		NodeList childs = doc.getChildNodes();
		Node hild = childs.item(0);

		Element testdiv = doc.createElement("div");
		testdiv.setAttribute("_ngprefix_ngIf", "abc=abc");
		testdiv.setAttribute("_ngbind0_data_ngbind1_", "data");
		testdiv.setAttribute("data_ngbind1_", "data");
		hild.appendChild(testdiv);

		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);

		System.out.println(writer.toString().replaceAll("_ngprefix_", "*"));

	}

	@Test
	public void testconvert() {

		ActionBeanObject bo = Mockito.mock(ActionBeanObject.class);

		Mockito.doReturn(List.of(new File(
				"D:\\Jonathan\\eclipse-workspaces\\201907\\plugin\\PluginCreation\\a_plugin_struts\\test\\a_plugin_struts\\exmple.xml")))
				.when(bo).getPageFiles();
		Mockito.doReturn("testForm").when(bo).getFormName();
		StrutsConverter converter = new StrutsConverter(bo, null);
		converter.convert();

	}
}
