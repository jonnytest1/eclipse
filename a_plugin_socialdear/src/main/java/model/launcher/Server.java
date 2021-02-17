package model.launcher;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonValue;

import cfg.SystemSettings;

public class Server {

	String id;

	List<Task> tasks = new ArrayList<>();

	Integer instances;

	String vhost;

	private String projectName;

	Branch branch;

	private String kibanaHost;

	Server(JsonObject server, String project, Branch branch) {
		this.projectName = project;
		this.branch = branch;
		id = server.getString("id").replace(SystemSettings.getMarathonGroupFilter(), "");
		instances = server.getInt("instances");

		JsonObject labels = server.getJsonObject("labels");

		if (server.containsKey("env")) {
			JsonObject environment = server.getJsonObject("env");
			if (environment.containsKey("MyHost")) {
				kibanaHost = environment.getString("MyHost");
			}
		}

		if (labels.containsKey("HAPROXY_0_VHOST")) {
			vhost = labels.getString("HAPROXY_0_VHOST");
		}

		for (JsonValue task : server.getJsonArray("tasks")) {
			tasks.add(new Task((JsonObject) task, this));
		}

	}

	public Server(String name, String host, String port, String projectString, String kibanaHost) {
		id = name;
		this.tasks.add(new Task(host, port, this, true));
		this.projectName = projectString;
		this.kibanaHost = kibanaHost;
	}

	public String getLongId() {
		return id;
	}

	public String getShortId() {
		return id.replace(SystemSettings.getMarathonGroupFilter(), "");
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public Integer getInstances() {
		return instances;
	}

	public String getVhost() {
		return vhost;
	}

	public String getProjectName() {
		return projectName;
	}

	public Branch getBranch() {
		return branch;
	}

	public String getKibanaHost() {
		return kibanaHost;
	}

	public boolean matchesFilter(String filter) {
		if ("".equals(filter) || id.equals("backend")) {
			return true;
		}
		return id.contains(filter);
	}
}
