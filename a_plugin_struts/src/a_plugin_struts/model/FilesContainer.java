package a_plugin_struts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesContainer {

	List<File> xslFiles = new ArrayList<>();
	List<File> xmlFiles = new ArrayList<>();
	List<File> javaFiles = new ArrayList<>();

	public void addXsl(File file) {
		xslFiles.add(file);

	}

	public void addXml(File file) {
		xmlFiles.add(file);

	}

	public void addJava(File file) {
		javaFiles.add(file);
	}

	public List<File> getXmlFiles() {
		return xmlFiles;
	}

	public int getSize() {
		return xmlFiles.size() + javaFiles.size() + xslFiles.size();
	}

	public List<File> getJavaFiles() {
		return javaFiles;
	}

}
