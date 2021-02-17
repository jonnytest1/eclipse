package cfg_tp;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;

/**
 * @since 2.1
 */
public class FileRepository {
	private FileRepository() {
		// default
	}

	public static final String GRADLE_DB_KEY = "docker.container";

	public static void setGradlePropertiesAttribute(IProject project, String key, String value) {
		IResource gradleFile = project.findMember("gradle.properties.local");
		if (gradleFile != null && gradleFile.exists()) {
			setFile(key, value, gradleFile);
			return;
		}
	}

	private static void setFile(String key, String value, IResource gradleFile) {
		File f = new File(gradleFile.getLocationURI());
		try {
			String previous = FileParser.readFile(f);
			StringBuilder edited = new StringBuilder();
			String[] split = previous.split("\n");
			for (int i = 0; i < split.length; i++) {
				String line = split[i];
				if (!line.startsWith("#") && line.split("=")[0].strip().equals(key)) {
					edited.append(key + "=" + value);
				} else {
					edited.append(line);
				}
				if (i != split.length - 1) {
					edited.append("\n");
				}
			}
			FileParser.writeFile(f, edited.toString());
			gradleFile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (IOException | CoreException e) {
			SystemProperties.print(e);
		}
	}

}
