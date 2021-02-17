package socialdear.views.component.implemented.customizable;

import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import socialdear.views.component.CustomElementPanel;

public abstract class ConfigurationPanel extends CustomElementPanel {

	private static final long serialVersionUID = 2794676296592704365L;

	protected transient JsonObjectBuilder settingsBuilder = Json.createObjectBuilder();

	protected transient JsonObject previousSettings;

	protected ConfigurationPanel(JsonObject settings) {
		this.previousSettings = settings;

		for (Entry<String, JsonValue> value : settings.entrySet()) {
			settingsBuilder.add(value.getKey(), value.getValue());
		}
	}

	public JsonObject getSettings() {
		return settingsBuilder.build();
	}

}
