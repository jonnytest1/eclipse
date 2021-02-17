package service;

import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.core.resources.IProject;

import cfg_tp.FileRepository;
import cfg_tp.SystemSettings;
import cfg_tp.SystemSettings.TP_SETTINGS;
import http.TargetprocessRepository;
import socialdear.logging.SystemProperties;
import socialdear.util.files.ManualResourceDelta;
import socialdear.util.files.ResourceMonitor;
import socialdear.util.files.ResourceMonitor.ResourceAttribute;

/**
 * @since 2.1
 */
public class GitListener {

	public GitListener() {
		if (SystemSettings.getBooleanValue(TP_SETTINGS.DBPICKER_LISTENER)) {
			registerGitDbListener();
		}

	}

	public void registerGitDbListener() {
		ResourceMonitor.registerResourceChangeListener(delta -> {
			if (!SystemSettings.getBooleanValue(TP_SETTINGS.DBPICKER_LISTENER)) {
				return false;
			}

			if (delta instanceof ManualResourceDelta) {
				IProject project = ((ManualResourceDelta) delta).getProject();
				String gitBranch = ResourceMonitor.getAttribute(project, ResourceAttribute.GIT);
				if (gitBranch.equals("master")) {
					updateDatabaseString(project, "livekopie");
				} else if (gitBranch.contains("release")) {
					updateDatabaseString(project, "nextRelease");
				}

				JsonObject userStory = new TargetprocessRepository().getUserStoryForBranch(gitBranch);
				if (userStory != null) {
					try {
						Optional<JsonObject> dbProperty = userStory.getJsonObject("customValues")
								.getJsonArray("customFields").stream()//
								.map(JsonValue::asJsonObject)//
								.filter(jsonValue -> jsonValue.getString("name").equals("DB")).findFirst();
						if (dbProperty.isPresent()) {
							String db = dbProperty.get().getString("value");
							updateDatabaseString(project, db);
						}
					} catch (Exception e) {
						SystemProperties.print("error with fetching for " + gitBranch, e);
					}
				}
				return false;
			}
			return false;
		});
	}

	private void updateDatabaseString(IProject project, String db) {
		FileRepository.setGradlePropertiesAttribute(project, FileRepository.GRADLE_DB_KEY, db);
		SystemProperties.notification("gradle proeprties db update",
				"set " + FileRepository.GRADLE_DB_KEY + " to " + db);
	}
}
