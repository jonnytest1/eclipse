package socialdear.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.widgets.Label;

import socialdear.logging.SystemProperties;
import socialdear.util.files.ZipUtil;

public class FXInstaller {

	private String path;
	private Label progressLabel;

	public FXInstaller(String path, Label progressLabel) {
		this.path = path;
		this.progressLabel = progressLabel;

	}

	public boolean install() {

		if (path != null) {
			// Files.copy(fx, new File(path).toPath());
			progressLabel.setText("extracting javafx from zip archive to " + path);
			try {
				ZipUtil.extractResourceToFile("/fx.zip", path);
			} catch (IOException e) {
				SystemProperties.print(e);
				return false;
			}
			SystemProperties
					.print(System.getProperty("eclipse.home.location").replace("file:/", "").replace("file:\\", ""));
			File eclipsePath = new File(
					System.getProperty("eclipse.home.location").replace("file:/", "").replace("file:\\", "")
							+ "/eclipse.ini");
			progressLabel.setText("adding modules to eclipse ini in  " + eclipsePath);

			String imoprtString = "\n--module-path " + path
					+ "\\lib\\ \n--add-modules javafx.swing,javafx.web,javafx.graphics,javafx.controls,javafx.base";

			try (FileWriter fr = new FileWriter(eclipsePath, true); BufferedWriter br = new BufferedWriter(fr)) {
				br.write(imoprtString);
			} catch (IOException e) {
				SystemProperties.print(e);
				return false;
			}
			return true;
		}
		return false;
	}
}
