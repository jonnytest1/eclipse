package socialdear.http.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.views.component.CustomElementPanel;

/**
 * 1.2
 */
public class Logging {

	public enum LogLevel {
		ERROR, DEBUG, INFO, WARNING
	}

	static Long timestamp = 0L;

	static List<LogMessage> logs = new ArrayList<>();

	static Long lastSent = 0L;

	private static ScheduledFuture<?> scheduledFuture;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private Logging() {
		// private
	}

	public static void schedulerLoop() {
		try {
			sendLogMessage();
		} catch (Exception e) {
			sendToConsole("error sending logmesage caught: " + e.getMessage(), e);
		}
		scheduledFuture = scheduler.schedule(Logging::schedulerLoop, 2, TimeUnit.SECONDS);
	}

	public static void logRequest(Map<String, String> message, LogLevel level, String consoleName,
			Throwable throwable) {
		Throwable logStackThrowable = new Throwable();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logStackThrowable.setStackTrace(stackTrace);

		StringWriter logWriter = new StringWriter();
		logStackThrowable.printStackTrace(new PrintWriter(logWriter));
		String logStack = logWriter.toString();

		Bundle bundle = null;

		for (StackTraceElement stack : stackTrace) {
			if (!stack.getClassName().contains("logging") && !stack.getClassName().contains("java.lang.Thread")) {
				try {
					Class<?> stackClass = Class.forName(stack.getClassName());
					bundle = FrameworkUtil.getBundle(stackClass);
					break;
				} catch (ClassNotFoundException e) {
					// nothing to do
				}

			}
		}

		if (throwable != null && Arrays.stream(throwable.getStackTrace())
				.anyMatch(stack -> stack.getClassName().equals(Logging.class.getCanonicalName()))) {

			return;
		}
		logs.add(new LogMessage(message, level, consoleName, throwable, Instant.now(), logStack, bundle));
		if (scheduledFuture == null) {
			schedulerLoop();
		}
	}

	public static void logRequest(String message, LogLevel level, String consoleName, Throwable throwable) {
		Map<String, String> map = new HashMap<>();
		map.put("message", message);
		logRequest(map, level, consoleName, throwable);

	}

	public static void sendLogMessage() {
		if (!logs.isEmpty()) {
			LogMessage sending = logs.remove(0);

			JsonObjectBuilder json = Json.createObjectBuilder()
					.add("application", System.getenv("DEBUG") != null ? "Eclipse Plugin Debug" : "Eclipse Plugin") //
					.add("timestamp", sending.getTime().toString()) //
					.add("Severity", sending.getLevel().toString());
			addWorkspace(json);
			addVersions(sending, json);

			sending.getMessage().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));

			if (sending.name != null) {
				json.add("name", sending.getName());
			}
			json.add("origin_log", sending.getLogStack());

			StringBuilder causePrefix = new StringBuilder();

			Throwable t = sending.getThrowable();
			while (t != null && t != t.getCause()) {
				String jsonMEssage = t.getMessage();
				if (jsonMEssage == null) {
					jsonMEssage = "";
				}

				json.add(causePrefix + "error_message", jsonMEssage);

				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				json.add(causePrefix + "error_stacktrace", exceptionAsString);

				t = t.getCause();
				causePrefix.append("cause_");
			}
			try {
				String jsonString = json.build().toString();
				String decoded = java.util.Base64.getEncoder().encodeToString(jsonString.getBytes());

				CustomResponse response = new CustomHttp()
						.target("https://pi4.e6azumuvyiabvs9s.myfritz.net/tm/libs/log/index.php").request()
						.post(decoded, "text/plain");
				if (response.getResponseCode() != 200) {
					sendToConsole("error on logging request with code:" + response.getResponseCode() + " - "
							+ response.getContent(), null);
				}
			} catch (IOException e) {

				sendToConsole("error logging to kibana with" + " caught: " + e.getMessage(), e);
			}

		}
	}

	private static void addVersions(LogMessage sending, JsonObjectBuilder json) {
		json.add("base_version", FrameworkUtil.getBundle(Logging.class).getVersion().toString());
		if (sending.getBundle() != null) {
			json.add("plugin", sending.getBundle().getSymbolicName()) //
					.add("version", sending.getBundle().getVersion().toString()); //
		}
	}

	private static void addWorkspace(JsonObjectBuilder json) {
		try {
			json.add("workspace", ResourcesPlugin.getWorkspace().getRoot().getLocation().lastSegment());
		} catch (Exception e) {
			json.add("workspace", "failed adding workspoace");
		}
	}

	private static MessageConsole createConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();

		IConsole[] consoles = conMan.getConsoles();
		for (IConsole c : consoles) {
			if (c.getName().equals(name)) {
				return (MessageConsole) c;
			}
		}

		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static void sendToConsole(String message) {
		sendToConsole(message, null);
	}

	public static void showMessage(String title, String message) {
		Display.getDefault()
				.syncExec(() -> MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message));
		Logging.logRequest(title + "\n" + message, LogLevel.INFO, null, null);
	}

	public static void sendToConsole(String message, Throwable throwable) {
		final MessageConsole console = createConsole("SocialDear Plugin");
		try (MessageConsoleStream out = console.newMessageStream()) {
			out.println(Thread.currentThread().getStackTrace()[1].toString() + "\n\t" + message);
		} catch (IOException e) {
			//
		}
		Logging.logRequest(message, LogLevel.INFO, null, throwable);

	}

	public static boolean showMessageOption(String title, String message, CustomElementPanel panel) {

		Map<String, Boolean> resultMap = new TreeMap<>();
		Display.getDefault()
				.syncExec(() -> resultMap.put("result", MessageDialog.openConfirm(getShell(panel), title, message)));
		Logging.logRequest(title + "\n" + message, LogLevel.INFO, null, null);
		return resultMap.get("result");

	}

	static Shell getShell(CustomElementPanel panel) {
		return panel.getRootView().getShell();
	}

	static class LogMessage {

		private Map<String, String> message;
		private LogLevel level;
		private String name;
		private Throwable throwable;

		private Instant now;
		private String logStack;
		private Bundle bundle;

		public LogMessage(Map<String, String> message, LogLevel level, String name, Throwable throwable, Instant now,
				String logStack, Bundle bundle) {
			this.message = message;
			this.level = level;
			this.name = name;
			this.throwable = throwable;
			this.now = now;
			this.logStack = logStack;
			this.bundle = bundle;
		}

		public Map<String, String> getMessage() {
			return message;
		}

		public LogLevel getLevel() {
			return level;
		}

		public String getName() {
			return name;
		}

		public Throwable getThrowable() {
			return throwable;
		}

		public Instant getTime() {
			return now;
		}

		public String getLogStack() {
			return logStack;
		}

		public Bundle getBundle() {
			return bundle;
		}

	}
}
