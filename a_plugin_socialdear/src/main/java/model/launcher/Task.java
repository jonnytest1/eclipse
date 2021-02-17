package model.launcher;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Task {

	String host;
	String port;

	String healthState;
	private Server server;

	boolean isJavaRemote = false;

	public Task(JsonObject task, Server server) {
		this.server = server;
		this.host = task.getString("host");
		this.healthState = task.getString("state");

		JsonArray ports = task.getJsonArray("ports");
		if (ports.size() > 1) {
			port = ports.get(1).toString();
			isJavaRemote = true;
		}

	}

	public Task(String host, String port, Server server, boolean isJavaRemote) {
		this.isJavaRemote = isJavaRemote;
		this.host = host;
		this.port = port;
		this.server = server;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getHealthState() {
		return healthState;
	}

	public Server getServer() {
		return server;
	}

	public boolean isJavaRemote() {
		return isJavaRemote;
	}

}
