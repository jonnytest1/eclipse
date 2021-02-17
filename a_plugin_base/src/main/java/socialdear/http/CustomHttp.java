package socialdear.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CustomHttp {

	public interface Auth {
		void addAuthentication(CustomHttp http);
	}

	private String target;

	Map<String, String> headers = new HashMap<>();

	Map<String, String> query = new HashMap<>();

	public CustomHttp() {
		// c
	}

	public CustomHttp target(String target) {
		this.target = target.replaceAll(" ", "%20");
		// .replaceAll("}", "%7D").replaceAll("{", "%7B").replaceAll("\\?",
		// "%3F")

		return this;
	}

	public CustomHttp auth(Auth auth) {
		auth.addAuthentication(this);
		return this;
	}

	public CustomHttp header(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public CustomHttp addQuery(String name, String value) {
		try {
			value = new URI("http://www.google.de?true=" + value).getRawQuery();
		} catch (URISyntaxException e) {
			value = value.replaceAll("\\?", "%3F");
			value = value.replaceAll(" ", "%20");
			value = value.replaceAll("'", "%27");
			value = value.replaceAll("ä", "%C3%A4");
			value = value.replaceAll("ö", "%C3%B6");
			query.put(name, value.replace("true=", ""));
			return this;
		}
		query.put(name, value.replace("true=", ""));
		return this;
	}

	public CustomHttpRequest request() throws IOException {
		Set<Entry<String, String>> querySet = query.entrySet();
		if (!querySet.isEmpty()) {
			if (!target.contains("?")) {
				target += "?";
			} else {
				target += "&";
			}
			Iterator<Entry<String, String>> iterator = querySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> queryParam = iterator.next();
				target += queryParam.getKey() + "=" + queryParam.getValue();
				if (iterator.hasNext()) {
					target += "&";
				}
			}

		}
		System.setProperty("http.maxRedirects", "10");
		HttpURLConnection con = (HttpURLConnection) new URL(target).openConnection();
		for (Entry<String, String> header : headers.entrySet()) {
			con.setRequestProperty(header.getKey(), header.getValue());
		}
		con.setInstanceFollowRedirects(true);

		return new CustomHttpRequest(con);
	}
}
