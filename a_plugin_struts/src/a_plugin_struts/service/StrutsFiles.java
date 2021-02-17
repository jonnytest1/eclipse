package a_plugin_struts.service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

import a_plugin_struts.model.ActionBeanObject;
import socialdear.util.files.FileParser;

public class StrutsFiles {

	private static final String _IMPORT_MODULE_POINT = "_import_module_point_";
	private static final String _IMPORT_ENTRYPOINT = "_import_entrypoint_";
	private static final String _IMPORT_CLASS_ENTRYPOINT = "_import_class_entrypoint_";
	private ActionBeanObject action;
	private StringBuilder pathlevelShared;
	private ConverterAttributes converterAttributes;

	public StrutsFiles(ActionBeanObject action, ConverterAttributes converterAttributes) {
		this.action = action;
		this.converterAttributes = converterAttributes;

		File findShared = converterAttributes.getConverterConfigDialog().getAngularPath();
		pathlevelShared = new StringBuilder();

		while (!hasChild(findShared, "shared")) {
			pathlevelShared.append("../");
			findShared = findShared.getParentFile();
		}

	}

	public void generateRestResource() {
		InputStream resourceAsStream = getClass().getResourceAsStream("restTemplate.txt");
		Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_8);
		String text = scanner.useDelimiter("\\A").next();

		text = text.replace("_java_entry_point_", converterAttributes.getJavaExecutes());

		List<String> restAttributes = converterAttributes.getRestAttributes().stream()//
				.distinct().sorted((s1, s2) -> s1.compareTo(s2))//
				.collect(Collectors.toList());

		boolean kippedComma = false;
		for (String entry : restAttributes) {
			if (kippedComma) {
				text = text.replace("_json_entrypoint_", ",_json_entrypoint_");
			} else {
				kippedComma = true;
			}
			text = text.replace("_json_entrypoint_", "\n					\"" + entry + "\"_json_entrypoint_");

		}

		text = fileReplacements(text);
		scanner.close();
		File f = new File(converterAttributes.getConverterConfigDialog().getWebservicePath() + "/" + converterAttributes.getComponentBaseUpperLettersCamel() + "Webservice.java");
		FileParser.writeFile(f, text);

	}

	public void generateModuleFile() {
		InputStream resourceAsStream = getClass().getResourceAsStream("moduelTemplate.txt");
		Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_8);
		String text = scanner.useDelimiter("\\A").next();

		// keep alphabetically sorted
		List<String> moduleList = List.of( //
				"bas-checkbox", "CheckboxModule", "shared/elements/checkbox/checkbox.module", //
				"bas-label", "LabelModule", "shared/elements/label/label.module", //
				"bas-breadcrumb", "BreadcrumbModule", "shared/generic-compounds/breadcrumb/breadcrumb.module", //
				"bas-button-line", "ButtonLineModule", "shared/generic-compounds/button-line/button-line.module", //
				"bas-field", "FieldModule", "shared/generic-compounds/field/field.module", //
				"bas-group", "GroupModule", "shared/generic-compounds/group/group.module", //
				"*", "MsgKeyModule", "shared/translate/msg-key.module", //
				"----------------------placeholder--------------" //
		);

		for (int i = 0; i < moduleList.size(); i += 3) {
			if (moduleList.get(i).equals("*") || converterAttributes.getTags().contains(moduleList.get(i))) {
				text = text.replace(_IMPORT_ENTRYPOINT, "import { " + moduleList.get(i + 1) + " } from '_path_level_shared_" + moduleList.get(i + 2) + "';\n_import_entrypoint_");
				text = text.replace(_IMPORT_MODULE_POINT, "		" + moduleList.get(i + 1) + ",\n_import_module_point_");
			}
		}

		text = text.replaceAll("_modulename_", converterAttributes.getModuleClassName());

		List<Entry<String, String>> entrySet = converterAttributes.getAngularComponentFiles().entrySet().stream()//
				.map(e -> {
					String relativePath = e.getValue().getAbsolutePath().replace(converterAttributes.getModuleFile().getParentFile().getAbsolutePath(), "");
					return Map.entry(e.getKey(), "." + relativePath.replace("\\", "/").replace(".ts", ""));
				}).sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))//
				.collect(Collectors.toList());
		for (Entry<String, String> file : entrySet) {
			text = text.replace(_IMPORT_CLASS_ENTRYPOINT, "import { " + file.getKey() + " } from '" + file.getValue() + "';\n_import_class_entrypoint_");
			text = text.replace("_name_of_component_for_module_", "		" + file.getKey() + ",\n_name_of_component_for_module_");
		}

		text = text.replace("_path_level_shared_", pathlevelShared.toString());
		text = text.replace("_service_class_", converterAttributes.getComponentBaseUpperLettersCamel() + "Service");
		text = text.replace("_service_import_file_", converterAttributes.getBaseName() + ".service");

		text = text.replace("_name_of_component_for_module_", "");
		text = text.replace(_IMPORT_CLASS_ENTRYPOINT, "");
		text = text.replace(_IMPORT_ENTRYPOINT, "");
		text = text.replace(_IMPORT_MODULE_POINT, "");
		scanner.close();
		FileParser.writeFile(converterAttributes.getModuleFile(), text);

	}

	void generateServiceFile() {
		InputStream resourceAsStream = getClass().getResourceAsStream("serviceTemplate.txt");
		Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_8);
		String text = scanner.useDelimiter("\\A").next();
		text = fileReplacements(text);
		scanner.close();
		FileParser.writeFile(new File(converterAttributes.getConverterConfigDialog().getAngularPath().getAbsolutePath() + "/" + converterAttributes.getBaseName() + ".service.ts"), text);

	}

	public void generateTypescriptFile(boolean needsService) {

		String file = needsService ? "tsTemplate.txt" : "tsTemplate_sub.txt";

		InputStream resourceAsStream = getClass().getResourceAsStream(file);
		Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_8);
		String text = scanner.useDelimiter("\\A").next();

		text = fileReplacements(text);

		converterAttributes.addAngularComponentFile();
		scanner.close();
		FileParser.writeFile(converterAttributes.getComponentFile(), text);
	}

	private String fileReplacements(String text) {
		String componentBase = converterAttributes.getComponentBaseUpperLettersCamel();
		String serviceClass = componentBase + "Service";
		String webClass = componentBase + "Webservice";

		text = text.replaceAll("_component_file_", converterAttributes.getBaseName());
		text = text.replaceAll("_class_name_", converterAttributes.getAngularComponentClassName());
		text = text.replaceAll("_service_class_", serviceClass);
		text = text.replaceAll("_selector_", converterAttributes.getSelector());
		text = text.replaceAll("_service_name_", serviceClass.toLowerCase());
		text = text.replaceAll("_restpath_", componentBase.toLowerCase());
		text = text.replaceAll("_service_import_file_", converterAttributes.getBaseName() + ".service");

		text = text.replaceAll("_webservice_class_", webClass);
		if (action != null) {
			text = text.replaceAll("_form_identifier_", action.getFormName());
			text = text.replaceAll("_form_class_qualified_", action.getFormBeanObject().getQualifiedFormClass());
			text = text.replaceAll("_form_name_", action.getFormBeanObject().getFormSourceFiles().get(0).getName().replace(".java", ""));
		}
		text = text.replaceAll("_path_level_shared_", pathlevelShared.toString());
		text = text.replaceAll("_form_action_str_", converterAttributes.getFormActionStr());
		text = text.replaceAll("_package_id_", "");

		text = text.replaceAll("_constructor_entrypoint_", "");
		text = text.replaceAll(_IMPORT_ENTRYPOINT, "");
		text = text.replaceAll("_method_entrypoint_", "");
		text = text.replaceAll(_IMPORT_MODULE_POINT, "");
		text = text.replaceAll("_json_entrypoint_", "");
		text = text.replaceAll("_service_import_line_", "");
		text = text.replaceAll("_constructor_param_", "");
		text = text.replaceAll(_IMPORT_CLASS_ENTRYPOINT, "");

		return text;
	}

	private boolean hasChild(File findShared, String string) {
		return new File(findShared.getAbsoluteFile() + "/" + string).exists();
	}
}
