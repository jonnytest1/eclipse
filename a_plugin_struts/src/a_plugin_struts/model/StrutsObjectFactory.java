package a_plugin_struts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import socialdear.logging.SystemProperties;

public class StrutsObjectFactory {

	private StrutsObjectFactory() {
		//
	}

	public static List<StrutsObject> parse(File file) {
		try {
			SystemProperties.print("parsing " + file.getAbsolutePath());
			Document document = PositionalXMLReader.readXML(file);
			return parseNode(document, file);
		} catch (Throwable e) {
			if (e.getMessage() == null || !e.getMessage().equals("Vorzeitiges Dateiende.")) {
				SystemProperties.print(file.getAbsolutePath(), e);
			}
			return new ArrayList<>();
		}

	}

	private static List<StrutsObject> parseNode(Node node, File file) {
		List<StrutsObject> obj = new ArrayList<>();
		NodeList children = node.getChildNodes();
		switch (node.getNodeName()) {
			case "form-bean":
				return List.of(new FormBeanObject(node, file));
			case "action":
				ActionBeanObject actionBeanObject = new ActionBeanObject(node, file);
				for (int i = 0; i < children.getLength(); i++) {
					List<StrutsObject> actionChildren = parseNode(children.item(i), file);
					actionChildren.parallelStream() //
							.filter(child -> child instanceof ForwardObject) //
							.map(c -> (ForwardObject) c)//
							.forEach(actionBeanObject::addForward);
					obj.addAll(actionChildren);
				}
				obj.add(actionBeanObject);
				return obj;
			case "forward":
				return List.of(new ForwardObject(node, file));
			case "#text":
			case "bouncer":
			case "allowed-get":
			case "parameter":
			case "set-property":
			case "plug-in":
				return List.of();
			case "#document":
			case "form-beans":
			case "struts-config":
			case "action-mappings":
			case "global-forwards":
				for (int i = 0; i < children.getLength(); i++) {
					obj.addAll(parseNode(children.item(i), file));
				}
				return obj;
			default:
				SystemProperties.print("unknown nodeNameType " + node.getNodeName());
				return List.of();
		}

	}
}
