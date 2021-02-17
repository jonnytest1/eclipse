package http;

import static socialdear.views.ImplementedPreferences.getValue;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonString;

import cfg_tp.SystemSettings.TP_SETTINGS;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;

/**
 * @since 2.1
 */
public class PortainerRepository {

	public PortainerRepository() {

		// constructor
	}

	private String getAuthToken() throws IOException {
		String portainerApi = getValue(TP_SETTINGS.PORTAINER_API);

		String username = getValue(TP_SETTINGS.PORTAINER_USERNAME);
		String password = getValue(TP_SETTINGS.PORTAINER_PASSWORD);

		CustomResponse response = new CustomHttp().target(portainerApi + "/auth").request() //
				.post(MessageFormat.format("{\"username\":\"{0}\",\"password\":\"{1}\"}", username, password),
						"application/json");
		return "Bearer " + response.json().asJsonObject().getString("jwt");
	}

	public List<String> getContainerNames() throws IOException {
		String portainerApi = getValue(TP_SETTINGS.PORTAINER_API);

		CustomResponse response = new CustomHttp().target(portainerApi + "/endpoints/1/docker/containers/json?all=1") //
				.header("Authorization", getAuthToken()) //
				.request() //
				.get();
		return response.json().asJsonArray().stream()//
				.flatMap(value -> value.asJsonObject().getJsonArray("Names").stream())//
				.map(jsonVAlue -> ((JsonString) jsonVAlue).getString()) //
				.collect(Collectors.toList());
	}

}
