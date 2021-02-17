package socialdear.util.files;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import socialdear.http.logging.Logging.LogLevel;
import socialdear.logging.SystemProperties;
import socialdear.util.Executer;
import socialdear.util.ExecutionException;

public class ResourceMonitor {

	private ResourceMonitor() {
		// constructor
	}

	private static List<IResourceDeltaVisitor> listeners = new ArrayList<>();

	private static Map<String, Map<ResourceAttribute, String>> attributes = new HashMap<>();

	private static Map<String, Boolean> gitMap = new HashMap<>();

	public enum ResourceAttribute {
		GIT
	}

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 * IResourceDeltaVisitor true if the resource delta's children should be
	 * visited; false if they should be skipped.
	 * 
	 * @param onChanges
	 */
	public static void registerResourceChangeListener(IResourceDeltaVisitor onChanges) {
		if (listeners.isEmpty()) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> handleDelta(event.getDelta()));

			scheduler.scheduleAtFixedRate(ResourceMonitor::checkResources, 0, 2, TimeUnit.SECONDS);
		}
		listeners.add(onChanges);
	}

	private static void handleDelta(IResourceDelta delta) {
		if (delta != null) {
			for (IResourceDeltaVisitor visitor : listeners) {
				try {
					delta.accept(visitor);
				} catch (CoreException e) {
					SystemProperties.print(e);
				}
			}
		}
	}

	private static void checkResources() {
		try {
			for (IProject project : FileParser.getProjects()) {
				checkProject(project);
			}
		} catch (RuntimeException e) {
			SystemProperties.print(e);
		}
	}

	private static void checkProject(IProject project) {
		if (project == null) {
			SystemProperties.print(LogLevel.INFO, "project is null");
			return;
		}

		String projectLocation = project.getLocation().makeAbsolute().toFile().toString();
		if (!project.getLocation().toFile().exists() || gitMap.containsKey(projectLocation)) {
			return;
		}
		try {

			String output = new Executer().run("git rev-parse --abbrev-ref HEAD", project.getLocationURI());
			if (output == null) {
				SystemProperties.print(LogLevel.INFO, "executer returned null");
				return;
			}
			String[] outputparts = output.trim().split("\n");
			if (outputparts.length != 2) {
				SystemProperties.print(LogLevel.INFO, "weird output format " + output);
				return;
			}
			String branch = outputparts[1];
			String oldBranch = attributes.computeIfAbsent(projectLocation, k -> new EnumMap<>(ResourceAttribute.class))
					.computeIfAbsent(ResourceAttribute.GIT, key -> branch);

			if (!oldBranch.equals(branch)) {
				attributes.get(projectLocation).put(ResourceAttribute.GIT, branch);
				handleDelta(new ManualResourceDelta(project));
			}
		} catch (ExecutionException e) {
			if (e.getError().contains("ot a git repository")) {
				gitMap.put(projectLocation, true);
				// not a git repro skip
			} else if (e.getText().contains("IOException in Executer")) {
				// not a valid project
			} else {
				SystemProperties.print(e);
			}
		}
	}

	public static String getAttribute(IProject project, ResourceAttribute att) {
		return attributes.get(project.getLocation().makeAbsolute().toFile().toString()).get(att);
	}

}
