package util;

import static socialdear.views.ImplementedPreferences.getValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import cfg.SystemSettings.SD_SETTINGS;
import model.launcher.Branch;
import model.launcher.ELKLoggingModel;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;
import views.logging.LoggingElementViewMessagesPanel;

public class KibanaLogService {

	private static Map<String, Branch> loggingEnabled = new HashMap<>();

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private static boolean schedulerRunning = false;

	private KibanaLogService() {

	}

	public static boolean switchLogging(Branch branch) {
		Branch isEnabled = loggingEnabled.get(branch.getName());
		if (isEnabled == null) {
			SystemProperties.print("enabled logs for " + branch.getName());
			loggingEnabled.put(branch.getName(), branch);
		} else {
			SystemProperties.print("disabled logs for " + branch.getName());
			loggingEnabled.put(branch.getName(), null);
		}
		return isEnabled != null;
	}

	public static boolean getLogging(Branch branch) {
		Branch isEnabled = loggingEnabled.get(branch.getName());
		return isEnabled != null;

	}

	public static void getLogs() {
		if (!schedulerRunning) {
			new Thread(KibanaLogService::fetchLogs).start();

		}
	}

	public static void fetchLogs() {
		boolean isLogging = false;
		for (Entry<String, Branch> entry : loggingEnabled.entrySet()) {
			isLogging = fetchEntry(entry, isLogging);
		}
		if (isLogging) {
			scheduler.schedule(KibanaLogService::getLogs, 5, TimeUnit.SECONDS);
		} else {
			schedulerRunning = false;
		}
	}

	private static Boolean fetchEntry(Entry<String, Branch> entry, boolean isLogging) {

		if (entry.getValue() != null) {
			isLogging = true;

			Long endTime = System.currentTimeMillis();
			Long startTime = endTime - (60 * 1000 * 20);

			if (entry.getValue().getLastLogFetch() != null) {
				startTime = entry.getValue().getLastLogFetch();
			}

			String postBody = "{\"index\":\"*\",\"ignore_unavailable\":true,\"timeout\":30000,\"preference\":1547816400878}\n"
					+ "{\"version\":true,\"size\":500,\"sort\":[{\"@timestamp\":{\"order\":\"desc\",\"unmapped_type\":\"boolean\"}}],\"_source\":{\"excludes\":[]},\"aggs\":{\"2\":{\"date_histogram\":{\"field\":\"@timestamp\",\"interval\":\"30s\",\"time_zone\":\"Europe/Berlin\",\"min_doc_count\":1}}},\"stored_fields\":[\"*\"],\"script_fields\":{\"applicationmapped\":{\"script\":{\"inline\":\"def m=doc['application.keyword'].value;\\n   return m;\\n}\",\"lang\":\"painless\"}}},\"docvalue_fields\":[\"@timestamp\",\"updated_at\",\"url.accessDate\",\"url.createDate\"],\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"query\":\"application:(\\\""
					+ entry.getValue().getBackEndHost()
					+ "\\\")\",\"analyze_wildcard\":true,\"default_field\":\"*\"}},{\"range\":{\"@timestamp\":{\"gte\":"
					+ startTime + ",\"lte\":" + endTime
					+ ",\"format\":\"epoch_millis\"}}}],\"filter\":[],\"should\":[],\"must_not\":[]}},\"highlight\":{\"pre_tags\":[\"@kibana-highlighted-field@\"],\"post_tags\":[\"@/kibana-highlighted-field@\"],\"fields\":{\"*\":{}},\"fragment_size\":2147483647}}\n";

			String responseBody;
			try {
				CustomResponse response = new CustomHttp()
						.target(getValue(SD_SETTINGS.ELKHOST) + "/elasticsearch/_msearch")
						.header("kbn-version", "6.3.0").request().post(postBody, "application/x-ndjson");
				responseBody = response.getContent();
			} catch (IOException e) {

				SystemProperties.print(e);
				try {
					responseBody = new String(Files.readAllBytes(Paths.get(
							"D:\\Jonathan\\Projects\\eclipse\\plugins\\testMigration\\src\\resources\\KibanaFallBackEntries.json")));
				} catch (IOException e1) {
					return isLogging;
				}
			}

			try (JsonReader createReader = Json.createReader(new ByteArrayInputStream(responseBody.getBytes()))) {
				JsonObject root = createReader.readObject();
				JsonArray kibanaMessages = root.getJsonArray("responses").getJsonObject(0).getJsonObject("hits")
						.getJsonArray("hits");

				List<ELKLoggingModel> newEntries = kibanaMessages.stream()
						.map(msg -> new ELKLoggingModel((JsonObject) msg)).collect(Collectors.toList());

				LoggingElementViewMessagesPanel.addLogEntries(entry.getValue().getName(), newEntries);
			}
		}
		return isLogging;

	}

}
