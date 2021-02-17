package views.launcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.swing.BoxLayout;

import cfg.SystemSettings;
import model.launcher.Branch;
import model.launcher.Server;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;
import socialdear.views.component.CustomElementPanel;
import views.logging.LoggingElementView;

public class LauncherContainerComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4150687770515761116L;

	private transient String projectName;

	transient ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	Long lastFetchedBrenches = null;

	private String filter = "";

	private List<Branch> branchesList;

	public LauncherContainerComponent() {

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		recreate();

	}

	public void setActiveProjectName(String projectName) {
		this.projectName = projectName;
		recreate();
	}

	@Override
	protected void addElements() {

		boolean needsRefetch = false;

		branchesList = getBranches();

		LoggingElementView.setBranchNames(branchesList);

		boolean colored = false;
		for (Branch branch : branchesList) {
			if (!branch.getName().equals("/storybranches") && branch.matchesFilter(filter)) {
				colored = !colored;
				LauncherBranchContainerComponent branchComponent = new LauncherBranchContainerComponent(branch, colored,
						filter);
				if (branchComponent.needsReFetch()) {
					needsRefetch = true;
				}
				add(branchComponent);
			}
		}

		if (needsRefetch) {
			scheduler.schedule(this::recreate, 20L, TimeUnit.SECONDS);
		} else {
			scheduler.schedule(this::recreate, 5, TimeUnit.MINUTES);
		}
	}

	List<Branch> getBranches() {
		if (lastFetchedBrenches == null || System.currentTimeMillis() - lastFetchedBrenches > 1000 * 20) {
			branchesList = new ArrayList<>();
			branchesList.add(new Branch("localhost",
					Arrays.asList(new Server("backend", "localhost", "8787", projectName, "jonathan"))));
			String response = getMarathonJson();
			try (JsonReader createReader = Json.createReader(new ByteArrayInputStream(response.getBytes()))) {
				JsonObject root = createReader.readObject();
				addBranches(root, branchesList);
				lastFetchedBrenches = System.currentTimeMillis();
				return branchesList;
			} catch (Exception e) {
				if (response.length() < 1000) {
					SystemProperties.print(response, e);
				} else {
					SystemProperties.print(e);
				}

				return branchesList;
			}
		} else {
			return branchesList;
		}
	}

	private String getMarathonJson() {
		try {
			String url = SystemSettings.getMarathonGroupsUrl()
					+ "?embed=group.groups&embed=group.apps&embed=group.apps.tasks&embed=group.apps.deployments&embed=group.apps.taskStats&embed=group.apps.counts";

			CustomResponse requestRespopnse = new CustomHttp().target(url)
					.header("Authorization", SystemSettings.getMarathonHeader()).request().get();
			return requestRespopnse.getContent();
		} catch (IOException e) {
			SystemProperties.print(e);

			try {
				return FileParser.getFile("fallbackjsons/marathon.json", this);
			} catch (IOException e1) {
				return "[]";
			}

		}
	}

	private void addBranches(JsonObject root, List<Branch> branches) {

		JsonArray groups = root.getJsonArray("groups");
		JsonArray apps = root.getJsonArray("apps");

		for (JsonValue folder : groups) {
			addBranches((JsonObject) folder, branches);
		}
		if (!apps.isEmpty()) {
			branches.add(new Branch(root, projectName));
		}

	}

	public void setFilter(String text) {
		filter = text;
		recreate();

	}
}
