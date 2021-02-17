package socialdear.logging;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.IDE;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;

public class SystemProperties {

	static Long lastDisplayForce = 0L;

	static String consoleName = "socialDearPlugin";

	private static IWorkbenchPage workbench;

	private SystemProperties() {
		//
	}

	public static void notification(String title, String text) {
		Display.getDefault().asyncExec(() -> new TextNotification(Display.getCurrent(), title, text).open());

	}

	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] {myConsole});
		return myConsole;
	}

	public static void print(String message, Throwable t) {
		print(LogLevel.ERROR, message, t);

	}

	public static void print(LogLevel level, String message, Throwable t) {

		print(level, message + "\ncause :" + t.getCause() + "\n" + t.toString() + "\n" + Arrays.stream(t.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")), (String) null, null, t);
	}

	public static void print(LogLevel level, String message) {
		print(level, message, (String) null, null, null);
	}

	public static void print(LogLevel level, Throwable e) {
		print(level, "caught exception", e);
	}

	public static void printInfo(Throwable e) {
		print(LogLevel.INFO, "caught exception", e);

	}

	public static void print(String message) {
		print(LogLevel.INFO, message);

	}

	public static void print(Throwable e) {
		print(LogLevel.ERROR, e);

	}

	public static void print(LogLevel level, String message, String consoleNameStr, RGB color, Throwable t) {

		String consoleNameString = consoleName;
		if (consoleNameStr != null) {
			consoleNameString = consoleNameStr;
		}
		MessageConsole console = findConsole(consoleNameString);

		Display.getDefault().asyncExec(() -> {
			try (MessageConsoleStream newMessageStream = console.newMessageStream()) {
				switch (level) {
					case DEBUG:
						break;
					case ERROR:
						newMessageStream.setColor(new Color(Display.getCurrent(), new RGB(255, 0, 0)));
						break;
					case INFO:
						newMessageStream.setColor(new Color(Display.getCurrent(), getINFOColor(color)));
						break;
					case WARNING:
						newMessageStream.setColor(new Color(Display.getCurrent(), getWarningColor(color)));
				}
				newMessageStream.println(message);
				if (level == LogLevel.ERROR && lastDisplayForce < System.currentTimeMillis() - (1000 * 60 * 5)) {
					displayConsole(console);
					lastDisplayForce = System.currentTimeMillis();
				}
			} catch (IOException e) {
				//
			}
		});
		if (level != LogLevel.DEBUG || System.getenv("DEBUG") != null) {
			Logging.logRequest(message, level, consoleNameStr, t);
		}

	}

	private static RGB getWarningColor(RGB color) {
		return color == null ? new RGB(255, 255, 0) : color;
	}

	private static RGB getINFOColor(RGB color) {
		return color == null ? new RGB(0, 0, 0) : color;
	}

	static void displayConsole(MessageConsole findConsole) {
		try {
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			if (workbench != null) {
				IConsoleView view = (IConsoleView) workbench.showView(id);
				view.display(findConsole);
			}
		} catch (PartInitException e) {
			//
		}
	}

	/**
	 * @since 3.0
	 */
	public static void openMarker(IMarker marker) {
		if (marker == null) {
			SystemProperties.print("marker is null");
			return;
		}

		Display.getDefault().asyncExec(() -> {
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker, true);
			} catch (PartInitException | IllegalArgumentException e1) {
				SystemProperties.print(marker + "", e1);
			}
		});
	}

	public static void setWorkbench(IWorkbenchPage page) {
		workbench = page;

	}

	public static int displayConfirmDialog(String message, String title) {
		return displayConfirmDialog(message, title, new String[] {"Ok"});
	}

	public static int displayConfirmDialog(String message, String title, String[] strings) {
		MessageDialog confirm = new MessageDialog(null, title, null, message, MessageDialog.QUESTION, strings, 0);
		return confirm.open();
	}

}
