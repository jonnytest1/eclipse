package a_plugin_struts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;

public class FormBeanObject extends StrutsObject {

	private String name;
	private String type;
	private List<File> formSourceFiles = new ArrayList<>();
	private String fileRegex;

	public FormBeanObject(Node node, File file) {
		super(node, file);
		try {
			NamedNodeMap attributes = node.getAttributes();
			name = attributes.getNamedItem("name").getNodeValue();
			type = attributes.getNamedItem("type").getNodeValue();
			fileRegex = "(.*)" + type.replaceAll("\\.", ("/")) + "(.*)";
		} catch (Exception e) {
			SystemProperties.print("error in setting attributes", e);
		}
	}

	public String getName() {
		return name;
	}

	public String getQualifiedFormClass() {
		return type;
	}

	public void addSourceFile(File file) {
		getFormSourceFiles().add(file);

	}

	public IMarker getClassMarker() {
		if (getFormSourceFiles().size() == 1) {
			projectFile = FileParser.createProjectFile(getFormSourceFiles().get(0));
			try {
				return projectFile.createMarker(IMarker.TEXT);
			} catch (CoreException e) {
				SystemProperties.print(e);
			}
		} else if (!getFormSourceFiles().isEmpty()) {
			SystemProperties.print("multiple files");
		}
		return null;
	}

	public String getFileRegex() {
		return fileRegex;
	}

	public List<File> getFormSourceFiles() {
		return formSourceFiles;
	}

}
