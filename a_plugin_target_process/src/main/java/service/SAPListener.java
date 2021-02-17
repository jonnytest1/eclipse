package service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;

import cfg_tp.SystemSettings;
import cfg_tp.SystemSettings.TP_SETTINGS;
import http.TargetprocessRepository;
import model.WorkEntry;
import socialdear.logging.SystemProperties;
import socialdear.util.Executer;
import socialdear.util.ExecutionException;
import socialdear.util.files.FileParser;
import socialdear.util.files.ManualResourceDelta;
import socialdear.util.files.ResourceMonitor;
import socialdear.util.files.ResourceMonitor.ResourceAttribute;
import socialdear.views.component.implemented.ProjectSelectionComponent;

/**
 * @since 2.1
 */
public class SAPListener {

	private static final List<WorkEntry> ENTRIES = new ArrayList<>();
	private static Map<String, String> sapIds = new HashMap<>();
	private static ProjectSelectionComponent projectSelection;

	public SAPListener() {

		if (SystemSettings.getBooleanValue(TP_SETTINGS.SAP_LISTENER)) {
			registerSAPListener();
		}
	}

	public static List<WorkEntry> getEntries() {
		return ENTRIES;
	}

	public static void clearEntries() {
		ENTRIES.clear();
		;
	}

	public void registerSAPListener() {
		ResourceMonitor.registerResourceChangeListener(this::resourceListener);
		boolean refreshProjectSelection = false;
		for (IProject project : FileParser.getProjects()) {
			try {
				String branch = getGitBranch(project);
				if (addEntry(project, branch)) {
					refreshProjectSelection = true;
				}

			} catch (ExecutionException e) {
				if (e.getError().contains("ot a git repository")) {
					// not a git repro skip
				} else if (e.getText().contains("IOException in Executer")) {
					// not a valid project
				} else {
					SystemProperties.print(e);
				}
			}
		}
		if (refreshProjectSelection && projectSelection != null) {
			projectSelection.recreate();
		}
	}

	private boolean addEntry(IProject project, String branch) throws ExecutionException {
		boolean refreshProjectselection = ENTRIES.stream().noneMatch(e -> e.getProject() == project);
		ENTRIES.add(getWorkEntry(project, branch));
		return refreshProjectselection;

	}

	public static String getGitBranch(IProject project) throws ExecutionException {
		URI location = project.getLocationURI();
		String gitResponse = new Executer().run("git rev-parse --abbrev-ref HEAD", location);
		return gitResponse.trim().split("\n")[1];

	}

	private boolean resourceListener(IResourceDelta delta) {
		if (!SystemSettings.getBooleanValue(TP_SETTINGS.SAP_LISTENER)) {
			return false;
		}

		if (delta instanceof ManualResourceDelta) {
			IProject project = ((ManualResourceDelta) delta).getProject();
			String gitBranch = ResourceMonitor.getAttribute(project, ResourceAttribute.GIT);
			try {
				addEntry(project, gitBranch);
			} catch (ExecutionException e) {
				SystemProperties.print(e);
			}
			return false;
		}
		return false;
	}

	public static WorkEntry getWorkEntry(IProject project, String gitBranch) throws ExecutionException {
		if (gitBranch == null) {
			gitBranch = getGitBranch(project);
		}

		String sapId = sapIds.get(gitBranch);
		if (sapId != null) {
			return new WorkEntry(sapId, gitBranch, project);
		} else {
			String sap = getSAPId(gitBranch);
			if (sap == null) {
				sap = "undefined";
			}
			sapIds.put(gitBranch, sap);
			return new WorkEntry(sap, gitBranch, project);
		}
	}

	private static String getSAPId(String gitBranch) {
		JsonObject userStory = new TargetprocessRepository().getUserStoryForBranch(gitBranch);
		if (userStory != null) {
			Optional<JsonObject> sapProperty = userStory.getJsonObject("customValues").getJsonArray("customFields")
					.stream()//
					.map(JsonValue::asJsonObject)//
					.filter(jsonValue -> jsonValue.getString("name").equals("SAP Projekt")).findFirst();
			if (sapProperty.isPresent()) {
				return sapProperty.get().getString("value");
			}
		}
		return null;

	}

	public static void setProjectSelection(ProjectSelectionComponent projectSelectionComponent) {
		projectSelection = projectSelectionComponent;

	}

}
