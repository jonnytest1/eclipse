package a_plugin_struts.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.model.FilesContainer;

public class ConverterAttributes {

	private String actionString;

	private List<String> tags = new ArrayList<>();
	private List<String> restAttributes = new ArrayList<>();

	private File convertingFile;

	private ConverterConfigDialog converterConfigDialog;

	private File moduleFile;

	private String convertingFileBaseName;

	private File componentFile;

	private String angularComponentBaseUpperFirstLetters;

	private String navbarkey;

	private String angularbaseName;

	private Map<String, File> angularComponentFilesForModule = new HashMap<>();

	private File relativeTo;

	private File lessFile;

	private String javaExecutes = "";

	private FilesContainer files;

	public ConverterAttributes(ConverterConfigDialog converterConfigDialog, FilesContainer files2) {
		this.converterConfigDialog = converterConfigDialog;
		files = files2;
	}

	public ConverterAttributes(ConverterAttributes attributes) {
		this.converterConfigDialog = attributes.converterConfigDialog;
		this.files = attributes.files;
	}

	public void setConvertingFile(File file) {
		this.convertingFile = file;
		String convertingFileName = file.getName();
		this.convertingFileBaseName = convertingFileName.replace(".xml", "");

		angularbaseName = converterConfigDialog.getAngularPath().getAbsolutePath() + "/" + convertingFileBaseName;
		if (relativeTo != null) {
			angularbaseName += "/" + convertingFileBaseName;
		}
		moduleFile = new File(angularbaseName + ".module.ts");
		componentFile = new File(angularbaseName + ".component.ts");
		lessFile = new File(angularbaseName + ".component.less");

		this.angularComponentBaseUpperFirstLetters = Arrays.asList(convertingFileBaseName.split("-"))//
				.parallelStream() //
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(""));
	}

	public void setFormActionString(String string) {
		this.actionString = string;

	}

	public String getFormActionStr() {
		return this.actionString;
	}

	public List<String> getTags() {
		return tags;
	}

	public List<String> getRestAttributes() {
		return restAttributes;
	}

	public File getModuleFile() {
		return moduleFile;

	}

	public ConverterConfigDialog getConverterConfigDialog() {
		return converterConfigDialog;
	}

	public String getBaseName() {
		return convertingFileBaseName;
	}

	public File getComponentFile() {
		return componentFile;
	}

	public String getComponentBaseUpperLettersCamel() {
		return angularComponentBaseUpperFirstLetters;
	}

	public String getModuleClassName() {
		return getComponentBaseUpperLettersCamel() + "Module";
	}

	public String getAngularComponentClassName() {
		return getComponentBaseUpperLettersCamel() + "Component";
	}

	public File getConvertingFile() {
		return convertingFile;
	}

	public void setNavbarKey(String navbarkey) {
		this.navbarkey = navbarkey;

	}

	public String getNavbarkey() {
		return navbarkey;
	}

	public String getSelector() {
		return "bas-" + convertingFileBaseName.toLowerCase();
	}

	public File getComponentHTMLFile() {
		String customers = StrutsProperties.getValue(StrutsProperties.STRUTS_SETTINGS.CUSTOMERS);
		for (String customer : customers.split(",")) {
			if (getConvertingFile().getAbsolutePath().toLowerCase().matches("(.*)_" + customer.toLowerCase() + "(.*)")) {
				return new File(angularbaseName + "." + customer + ".component.html");
			}
		}
		return getComponentHTMLFileWithoutCustomer();
	}

	public File getComponentHTMLFileWithoutCustomer() {
		return new File(angularbaseName + ".component.html");
	}

	public Map<String, File> getAngularComponentFiles() {
		return angularComponentFilesForModule;
	}

	public void addAngularComponentFile() {
		angularComponentFilesForModule.put(getAngularComponentClassName(), getComponentFile());

	}

	public File getAngularBaseNameAsFile() {
		return new File(angularbaseName);
	}

	public void setRelativeTo(File angularBaseNameAsFile) {
		this.relativeTo = angularBaseNameAsFile;

	}

	public File getLessFile() {
		return lessFile;
	}

	public void addJavaExecute(String textContent) {
		this.javaExecutes += "\n" + textContent;
	}

	public void assign(ConverterAttributes subAttributes) {
		if (!subAttributes.javaExecutes.isBlank()) {
			javaExecutes += "\n" + subAttributes.javaExecutes;
		}
		getAngularComponentFiles().putAll(subAttributes.getAngularComponentFiles());
		getRestAttributes().addAll(subAttributes.getRestAttributes());
	}

	public String getJavaExecutes() {
		return javaExecutes;
	}

	public List<File> getXmlFiles() {
		return files.getXmlFiles();
	}
}
