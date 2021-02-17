import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;

import org.junit.jupiter.api.Test;

public class jsonTest {

	@Test
	public void testJsonStringARry() {
		try (JsonReader createReader = Json
				.createReader(new ByteArrayInputStream("{\"a\":[\"test\",\"abc\"]}".getBytes()))) {
			JsonStructure val = createReader.read();
			JsonObject obj = val.asJsonObject();
			JsonArray array = obj.getJsonArray("a");
			List<String> str = array.stream() //
					.map(jsVal -> {
						return ((JsonString) jsVal).getString();
					}).collect(Collectors.toList());
			System.out.print(str);
		}
	}
}
