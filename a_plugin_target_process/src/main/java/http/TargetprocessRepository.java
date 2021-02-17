package http;

import static socialdear.views.ImplementedPreferences.getValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import cfg_tp.SystemSettings;
import cfg_tp.SystemSettings.TP_SETTINGS;
import model.Team;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;
import views.current.widgets.bugview.BugViewOptions.LABELS;

/**
 * @since 2.1
 */
public class TargetprocessRepository {

	private static final String FILTER = "filter";

	private static final String ITEMS = "items";

	private static final String TP_HOST = "https://" + getValue(TP_SETTINGS.TARGET_PROCESS_DOMAIN) + "/api/v2/";

	private final String userStorySelection = "{id,name,NumericPriority,customValues" + allEntityStateProperties()
			+ "}";

	public List<Team> getTeams() {

		String url = TP_HOST + "Team?select={id,name}";

		try {
			CustomHttp request = new CustomHttp().target(url).addQuery("take", "1000");
			CustomResponse projects = request.auth(SystemSettings.getAuth()).request().get();
			List<Team> teams = new ArrayList<>();
			if (projects.getResponseCode() > 399) {
				return new ArrayList<>();
			}
			projects.json().asJsonObject().getJsonArray(ITEMS).forEach(
					obj -> teams.add(new Team(obj.asJsonObject().getInt("id"), obj.asJsonObject().getString("name"))));
			return teams;
		} catch (IOException e) {
			return null;
		}

	}

	String allEntityStateProperties() {
		return ",EntityState:{" //
				+ "EntityState.Id," //
				+ "EntityState.Name," //

				+ "EntityState.PreviousStates," //
				+ "EntityState.NextStates," //
				+ "EntityState.Workflow.Id as WorkflowId," //
				+ "EntityState.SubEntityStates.Select(Workflow.Id) as SubEntityStatesWorkflowIds" //
				+ "}";
	}

	public JsonArray getUserStoriesDetailed(JsonObject settings) {
		String url = TP_HOST + "UserStory?select=";

		String selection = userStorySelection;

		try {
			CustomHttp request = new CustomHttp().target(url + selection).addQuery("take", "20");

			String filter = "?Team.id is " + SystemSettings.getTeamId();
			if (settings.containsKey(LABELS.FILTER.name())) {
				filter += " and (" + settings.getString(LABELS.FILTER.name()) + ")";
				// .addQuery("filter", "?(TeamIteration is Current or TeamIteration is Future)")
			}
			request.addQuery(FILTER, filter);

			if (settings.containsKey(LABELS.SORTING.name())) {
				request.addQuery("orderby", settings.getString(LABELS.SORTING.name()));
			}
			CustomResponse projects = request.auth(SystemSettings.getAuth()).request().get();
			return projects.json().asJsonObject().getJsonArray(ITEMS);
		} catch (IOException e) {
			SystemProperties.print(e);
			return null;
		}
	}

	JsonArray getUserStories(JsonObject settings) {
		String url = TP_HOST + "UserStory?select=";

		String selection = userStorySelection;
		try {
			CustomHttp request = new CustomHttp().target(url + selection).addQuery("take", "1000");

			String filter = "?Team.id is " + SystemSettings.getTeamId();
			if (settings.containsKey(LABELS.FILTER.name())) {
				filter += "and (" + settings.getString(LABELS.FILTER.name()) + ")";
				// .addQuery("filter", "?(TeamIteration is Current or TeamIteration is Future)")
			}
			request.addQuery(FILTER, filter);

			if (settings.containsKey(LABELS.SORTING.name())) {
				request.addQuery("orderby", settings.getString(LABELS.SORTING.name()));
			}
			CustomResponse projects = request.auth(SystemSettings.getAuth()).request().get();
			return projects.json().asJsonObject().getJsonArray(ITEMS);
		} catch (IOException e) {
			SystemProperties.print(e);
			return null;
		}
	}

	private JsonArray itemArray(CustomResponse response) {
		return response.json()//
				.asJsonObject()//
				.getJsonArray(ITEMS);

	}

	public JsonObject getUserStoryForBranch(String branch) {
		String selection = userStorySelection;
		try {
			JsonArray itemArray = itemArray(new CustomHttp().target(TP_HOST + "UserStory")//
					.addQuery("select", selection)//
					.addQuery(FILTER, "?(Branch is '" + branch + "')")//
					.addQuery("take", "100")//
					.auth(SystemSettings.getAuth())//
					.request()//
					.get());
			if (itemArray.isEmpty()) {
				return null;
			}
			return itemArray.getJsonObject(0);
		} catch (IOException e) {
			SystemProperties.print(e);
			return null;
		}

	}

}
