package a_plugin_struts.model;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Node;

import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;

public class StrutsObject {

	File file;
	protected IFile projectFile;

	int lineNumber;
	private Node node;
	private IMarker marker;
	protected IProject project;

	public StrutsObject(Node node, File file) {
		this.node = node;
		this.file = file;

		projectFile = FileParser.createProjectFile(file);
		try {
			marker = projectFile.createMarker(IMarker.TEXT);
			marker.setAttribute(IMarker.LINE_NUMBER, Integer.valueOf((String) node.getUserData("lineNumber")));
		} catch (CoreException e) {
			SystemProperties.print(e);
		}

	}

	public void setProject(IProject project) {
		this.project = project;

	}

	public IMarker getObjectMarker() {
		return marker;
	}

}
