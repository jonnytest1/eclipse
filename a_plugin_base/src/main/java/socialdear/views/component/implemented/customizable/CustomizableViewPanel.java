package socialdear.views.component.implemented.customizable;

import java.awt.GridBagConstraints;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public abstract class CustomizableViewPanel extends CustomElementPanel {

	private static final String SETTINGS = "settings";

	private static final long serialVersionUID = -2216509805930801271L;

	private GridBagConstraints constraints;

	private transient JsonObject panelOptions;

	public GridBagConstraints getConstraints() {
		return constraints;
	}

	public ConfigurationPanel getConfigurationOptions() {
		return null;
	}

	public void setConstraints(GridBagConstraints constraints) {
		this.constraints = constraints;
	}

	public CustomizableViewPanel getWithOffset(int xOff, int yOff,
			Map<Integer, Map<Integer, CustomizableViewPanel>> components) {
		if (components.get(getConstraints().gridx + xOff) == null) {
			return null;
		}
		return components.get(getConstraints().gridx + xOff).get(getConstraints().gridy + yOff);
	}

	public JsonObject toJson() {

		JsonObjectBuilder jsonB = Json.createObjectBuilder();
		jsonB.add("class", getClass().getName());
		jsonB.add("x", getConstraints().gridx);
		jsonB.add("y", getConstraints().gridy);
		if (panelOptions == null) {
			jsonB.add(SETTINGS, getDefaultPanelOptions());
		} else {
			jsonB.add(SETTINGS, panelOptions);
		}
		return jsonB.build();

	}

	public static CustomizableViewPanel fromJson(JsonValue jsonS, ClassLoader loader) {
		JsonObject jsonO = jsonS.asJsonObject();
		String className = jsonO.getString("class");
		try {
			Class<?> classObj = loader.loadClass(className);
			CustomizableViewPanel instance = (CustomizableViewPanel) classObj.getConstructor().newInstance();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = jsonO.getInt("x");
			c.gridy = jsonO.getInt("y");
			if (jsonO.containsKey(SETTINGS)) {
				instance.panelOptions = jsonO.getJsonObject(SETTINGS);
			}

			instance.setConstraints(c);
			return instance;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
		}
		return null;
	}

	public JsonObject getPanelOptions() {
		if (panelOptions == null) {
			return getDefaultPanelOptions();
		}
		return panelOptions;
	}

	public JsonObject getDefaultPanelOptions() {
		return Json.createObjectBuilder().build();
	};

	public void setPanelOptions(JsonObject panelOptions) {
		this.panelOptions = panelOptions;
	}
}
