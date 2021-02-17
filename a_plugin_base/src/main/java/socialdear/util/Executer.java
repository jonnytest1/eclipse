package socialdear.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import socialdear.logging.SystemProperties;

/**
 * 1.2
 */
public class Executer {

	private Thread t;
	private Process p;

	public Executer() {
		// con
	}

	public InputStream run(String path) {
		try {
			Process process = new ProcessBuilder(path).start();
			return process.getInputStream();
		} catch (IOException e) {
			SystemProperties.print(e);
		}
		return null;
	}

	public String run(String... path) throws ExecutionException {
		return run(Arrays.asList(path), (URI) null);
	}

	public String run(List<String> path) throws ExecutionException {
		return run(path, (URI) null);
	}

	public String run(List<String> path, String location) throws URISyntaxException, ExecutionException {
		return run(path, new URI(location));
	}

	public String run(String command, URI location) throws ExecutionException {
		return run(Arrays.asList(command), location);
	}

	public String run(List<String> path, URI location) throws ExecutionException {
		List<String> commands = new ArrayList<>();
		commands.add("cmd.exe");
		commands.add("/C");
		commands.addAll(path);
		String command = path.stream().collect(Collectors.joining(" "));
		ProcessBuilder builder = new ProcessBuilder(commands);
		if (location != null) {
			builder.directory(new File(location));
		}
		try {
			p = builder.start();

			String text = readINputStream(p.getInputStream());
			String error = readINputStream(p.getErrorStream());

			StringBuilder line = new StringBuilder(command + "\n");

			if (error.trim().length() > 0) {
				throw new ExecutionException(line.append(text).toString(), error);
			}

			p.destroy();
			p = null;
			line.append(text);
			return line.toString();
		} catch (IOException e) {
			if (p != null) {
				p.destroy();
				p = null;
			}
			throw new ExecutionException("IOException in Executer", e.getMessage());
		}

	}

	private String readINputStream(InputStream is) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		StringBuilder line = new StringBuilder();
		while (true) {
			String l = r.readLine();
			if (l == null) {
				break;
			} else {
				line.append(l);
				line.append("\n");
			}
		}
		try {
			is.close();
			r.close();
		} catch (Exception e) {
			SystemProperties.print(e);
		}
		return line.toString();
	}

	public String runThreaded(List<String> asList, String string) {
		t = new Thread(() -> {
			try {
				run(asList, string);
			} catch (URISyntaxException | ExecutionException e) {
				SystemProperties.print(e);
			} finally {
				t = null;
			}

		});
		t.start();
		return null;
	}

	public void interrupt() {
		p.destroy();
		p = null;

	}

	public BufferedReader runIntegrated(List<String> path, String location) {
		List<String> commands = new ArrayList<>();

		commands.addAll(path);
		ProcessBuilder builder = new ProcessBuilder(commands);
		if (location != null) {
			// commands.add("cd " + location + " \n");
			builder.directory(new File(location));
		}
		builder.redirectErrorStream(true);
		try {
			p = builder.start();

			return new BufferedReader(new InputStreamReader(p.getInputStream()));

		} catch (IOException e) {
			p.destroy();
			p = null;
			SystemProperties.print(e);
		}
		return null;
	}

}
