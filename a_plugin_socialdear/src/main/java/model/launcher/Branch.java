package model.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.core.MediaType;

import cfg.SystemSettings;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;

public class Branch {

	List<Server> servers = new ArrayList<>();

	String name;

	private Long lastLogFetch = 0L;

	public Branch(String name, List<Server> servers) {
		this.name = name;
		this.servers = servers;
		servers.forEach(server -> server.branch = this);
	}

	public Branch(JsonObject asJsonObject, String projectName) {
		this.name = asJsonObject.getString("id").replace(SystemSettings.getMarathonGroupFilter(), "");
		for (JsonValue app : asJsonObject.getJsonArray("apps")) {
			servers.add(new Server((JsonObject) app, projectName, this));
		}
	}

	public List<Server> getServers() {
		return servers;
	}

	public String getName() {
		return name;
	}

	public void scale(Integer amount) {
		for (Server server : getServers()) {
			String target = SystemSettings.getMarathonAppsUrl() + server.getShortId();
			String entity = "{\"instances\":" + amount + "}";

			try {
				CustomResponse response = new CustomHttp().target(target)
						.header("Authorization", SystemSettings.getMarathonHeader()).request()
						.put(entity, MediaType.APPLICATION_JSON);
				SystemProperties.print(response.getResponseCode() + " : " + response.getContent());
			} catch (IOException e) {
				SystemProperties.print(e);
			}

		}

	}

	public String getBackEndHost() {
		String apps = "";
		for (Server server : servers) {
			if (server.getKibanaHost() != null) {
				if (apps.isEmpty()) {
					apps = server.getKibanaHost();
				} else {
					apps += "\\\" OR \\\"" + server.getKibanaHost();
				}
			}
		}
		return apps;
	}

	public Long getLastLogFetch() {
		return lastLogFetch;
	}

	public void setLastLogFetch(Long lastLogFetch) {
		this.lastLogFetch = lastLogFetch;
	}

	@Override
	public boolean equals(Object obj) {

		return obj instanceof Branch && ((Branch) obj).getName() == getName();
	}

	public boolean matchesFilter(String filter) {
		if (filter.equals("")) {
			return true;
		}
		if (name.contains(filter)) {
			return true;
		}
		return getServers().stream().anyMatch(server -> server.matchesFilter(filter));
	}
}
