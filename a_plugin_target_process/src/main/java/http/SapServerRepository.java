package http;

import static socialdear.views.ImplementedPreferences.getValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import cfg_tp.SystemSettings;
import cfg_tp.SystemSettings.TP_SETTINGS;
import model.Commit;
import model.WorkDurationEntry;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;

/**
 * @since 2.1
 */
public class SapServerRepository {

	static final String SERVER_API_URL = getValue(TP_SETTINGS.SAR_SERVER_ENDPOINT);

	public void sendTimes(Map<String, WorkDurationEntry> times, Calendar dataDay) {
		try {
			JsonArrayBuilder json = Json.createArrayBuilder();
			for (Entry<String, WorkDurationEntry> entry : times.entrySet()) {
				WorkDurationEntry durationEntry = entry.getValue();

				String commitMessage = durationEntry.getCommits().stream() //
						.map(Commit::getMessage)//
						.collect(Collectors.joining("\n"));

				Date date = dataDay.getTime();
				String text = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date);

				JsonObjectBuilder builder = Json.createObjectBuilder();
				builder.add("comment", commitMessage);
				builder.add("duration", durationEntry.getDurationInt());
				builder.add("sapNumber", entry.getKey());
				builder.add("date", text);

				json.add(builder.build());
			}
			new CustomHttp() //
					.target(SERVER_API_URL + "booking/user") //
					.auth(SystemSettings.getSapServerAuth()) //
					.request().post(json.build().toString(), "application/json");
		} catch (IOException e) {
			SystemProperties.print(e);
		}

	}

	public String requestToken(String username, String password) {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("username", username);
		builder.add("passwort", password);

		try {
			CustomResponse response = new CustomHttp() //
					.target(SERVER_API_URL + "auth/token") //
					.request().post(builder.build().toString(), "application/json");
			if (response.getResponseCode() != 200) {
				return null;
			}
			return response.getContent();
		} catch (IOException e) {
			return null;
		}
	}

}
