package views.current.widgets.userstories;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import socialdear.logging.SystemProperties;

/**
 * @since 2.1
 */
public class UserStory {

	private static final String VALUE = "value";

	Integer id;

	String name;

	String currentState;

	String akzeptanz;

	String matchingBranch;

	String sapProject;

	int currentTimeValue = 0;

	public UserStory(JsonValue userStory) {

		JsonObject userStoryO = userStory.asJsonObject();

		id = userStoryO.getInt("id");
		name = userStoryO.getString("name");
		currentState = userStoryO.getJsonObject("entityState").getString("name");

		JsonArray customValues = userStoryO.getJsonObject("customValues").getJsonArray("customFields");
		for (JsonValue attribute : customValues) {
			JsonObject attOBj = attribute.asJsonObject();

			if (attOBj.get("name") != JsonValue.NULL && attOBj.containsKey(VALUE)
					&& attOBj.get(VALUE) != JsonValue.NULL) {

				if (attOBj.getString("name").equals("Akzeptanzkriterien")) {
					akzeptanz = attOBj.getString(VALUE);

					String[] lines = akzeptanz.split("\n");

					parseCritera(lines);
				}
				if (attOBj.getString("name").equals("SAP Projekt")) {
					sapProject = attOBj.getString(VALUE);
				}
			}

		}

	}

	private void parseCritera(String[] lines) {
		for (String line : lines) {
			if (line.contains("Branch") || line.contains("branch")) {
				if (line.contains("<a")) {
					matchingBranch = line.split("<a")[1].split(">")[1].split("</a")[0];
				} else {
					SystemProperties.print("branch format: " + line);
					matchingBranch = line;
				}

			}
		}
	}
}
