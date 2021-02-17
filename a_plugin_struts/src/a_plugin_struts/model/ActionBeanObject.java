package a_plugin_struts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;
import socialdear.views.component.implemented.Visualization.Visualisable;

public class ActionBeanObject extends StrutsObject implements Visualisable {

	private String formName;
	private String qualifiedActionClass;
	private String path;
	private FormBeanObject formBeanObject;
	private List<File> actionSourceFiles = new ArrayList<>();
	private String fileRegex;
	private Map<String, String> properties = new HashMap<>();
	private List<File> pageFiles;
	private List<ForwardObject> forwards = new ArrayList<>();
	private List<ForwardObject> fromForwards = new ArrayList<>();

	public ActionBeanObject(Node node, File file) {
		super(node, file);
		try {
			NamedNodeMap attributes = node.getAttributes();

			Node nameAttribute = attributes.getNamedItem("name");
			if (nameAttribute != null) {
				formName = nameAttribute.getNodeValue();
			}
			qualifiedActionClass = attributes.getNamedItem("type").getNodeValue();
			path = attributes.getNamedItem("path").getNodeValue();
			fileRegex = "(.*)" + qualifiedActionClass.replaceAll("\\.", ("/")) + "(.*)";

			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeName().equals("set-property") && child.getAttributes().getNamedItem("property") != null) {
					properties.put(child.getAttributes().getNamedItem("property").getNodeValue(), child.getAttributes().getNamedItem("value").getNodeValue());
				}
			}
		} catch (Exception e) {
			SystemProperties.print("error in setting attributes", e);
		}
	}

	public void addForward(ForwardObject f) {
		this.forwards.add(f);
		f.parentAction(this);
	}

	public String getFormName() {
		return formName;
	}

	public String getPage() {
		if (properties.containsKey("page")) {
			return properties.get("page");
		}
		return null;
	}

	public String getShortActionName() {
		String[] parts = getQualifiedActionClass().split("\\.");
		return parts[parts.length - 1];
	}

	public String getQualifiedActionClass() {
		return qualifiedActionClass;
	}

	public String getPath() {
		return path;
	}

	public IFile getProjectFile() {
		return projectFile;
	}

	public void setForm(FormBeanObject formBeanObject) {
		this.formBeanObject = formBeanObject;

	}

	public FormBeanObject getFormBeanObject() {
		return formBeanObject;
	}

	public IMarker getClassMarker() {
		if (actionSourceFiles.size() == 1) {
			projectFile = FileParser.createProjectFile(actionSourceFiles.get(0));
			try {
				return projectFile.createMarker(IMarker.TEXT);
			} catch (CoreException e) {
				SystemProperties.print(e);
			}
		} else if (!actionSourceFiles.isEmpty()) {
			SystemProperties.print("multiple files");
		}
		return null;
	}

	public void addSourceFile(File file) {
		this.actionSourceFiles.add(file);

	}

	public String getFileRegex() {
		return fileRegex;
	}

	public List<File> getPageFiles() {
		return pageFiles;
	}

	public void setPagesFiles() {
		pageFiles = new ArrayList<>();
	}

	public void addPageFile(File file) {
		pageFiles.add(file);
	}

	public List<ForwardObject> getForwards() {
		return forwards;
	}

	@Override
	public <T extends Visualisable> List<T> getConnections() {
		if (forwards == null) {
			return List.of();
		}
		return (List<T>) forwards;
	}

	@Override
	public String getNodeName() {
		String string = "action\n" + getPath();
		if (getPage() != null) {
			string += "\n" + getPage();
		}
		return string;
	}

	public void fromForward(ForwardObject forwardObject) {
		this.fromForwards.add(forwardObject);

	}

	@Override
	public <T extends Visualisable> List<T> getBackwardConnections() {
		return (List<T>) fromForwards;
	}

}
