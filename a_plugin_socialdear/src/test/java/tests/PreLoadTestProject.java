package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;

public class PreLoadTestProject {

	@Test
	public void prefetch() {

		assumeTrue("true".equals(System.getProperty("runPrefetch")));

		Long now = System.currentTimeMillis();
		Long thirtyDAys = 1000 * 60 * 60 * 24 * 30L;
		Long before = now - thirtyDAys;
		// @formatter:off
		String requestBody = "{\"index\":\"*\",\"ignore_unavailable\":true,\"timeout\":30000,\"preference\":1555097686556}\r\n"
				+ "{\"version\":true," + "\"size\":1," + "\"sort\":[" + "{\"@timestamp\":" + "{" + "\"order\":\"desc\","
				+ "\"unmapped_type\":\"boolean\"" + "}" + "}" + "]," + "\"_source\":{" + "\"excludes\":[]" + "},"
				+ "\"aggs\":{" + "\"2\":{" + "\"date_histogram\":{" + "\"field\":\"@timestamp\","
				+ "\"interval\":\"30s\"," + "\"time_zone\":\"Europe/Berlin\"," + "\"min_doc_count\":1" + "}" + "}"
				+ "}," + "\"stored_fields\":[\"*\"]," + "\"script_fields\":{}," + "\"docvalue_fields\":["
				+ "{\"field\":\"@timestamp\",\"format\":\"date_time\"},"
				+ "{\"field\":\"@timestmap\",\"format\":\"date_time\"},"
				+ "{\"field\":\"canvas-workpad.@created\",\"format\":\"date_time\"},"
				+ "{\"field\":\"canvas-workpad.@timestamp\",\"format\":\"date_time\"},"
				+ "{\"field\":\"updated_at\",\"format\":\"date_time\"},"
				+ "{\"field\":\"url.accessDate\",\"format\":\"date_time\"},"
				+ "{\"field\":\"url.createDate\",\"format\":\"date_time\"}" + "]," + "\"query\":{" + "\"bool\":{"
				+ "\"must\":[" + "{\"query_string\":" + "{" + "\"query\":\"message:javamodels\","
				+ "\"analyze_wildcard\": true," + "\"default_field\": \"*\"" + "}" + "}" + ",{\"range\":" + "{"
				+ "\"@timestamp\":{" + "\"gte\":" + before + "," + "\"lte\":" + now + ","
				+ "\"format\":\"epoch_millis\"" + "}" + "}" + "}" + "]," + "\"filter\":[]," + "\"should\":[],"
				+ "\"must_not\":[]" + "}" + "}," + "\"highlight\":{" + "\"pre_tags\":[\"@kibana-highlighted-field@\"],"
				+ "\"post_tags\":[\"@/kibana-highlighted-field@\"]," + "\"fields\":{\"*\":{}},"
				+ "\"fragment_size\":2147483647" + "}" + "}\n";
		// @formatter:on
		try {
			CustomResponse response = new CustomHttp().header("kbn-version", "6.5.1")
					.target("http://localhost/kana/elasticsearch/_msearch").request()
					.post(requestBody, "application/x-ndjson");
			try (JsonReader createReader = Json.createReader(new StringReader(response.getContent()))) {

				JsonObject json = createReader.readObject();
				JsonArray entries = json.getJsonArray("responses").getJsonObject(0).getJsonObject("hits")
						.getJsonArray("hits");

				assertTrue(!entries.isEmpty());
				JsonObject logElement = entries.getJsonObject(0).getJsonObject("_source");

				for (Entry<String, JsonValue> entry : logElement.entrySet()) {
					if (entry.getKey().contains("model")) {
						String content = logElement.getString(entry.getKey());

						File file = new File(
								"D:\\Jonathan\\Projects\\eclipse\\plugins\\testProjects\\TimesCopyForMigartionTest\\src\\main\\java\\"
										+ entry.getKey().replaceAll("\\.", "\\\\") + ".java");

						if (file.exists()) {
							assertTrue(file.delete());
						}
						assertTrue(file.createNewFile());
						FileUtils.writeStringToFile(file, content, Charset.defaultCharset());
					}
				}
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
