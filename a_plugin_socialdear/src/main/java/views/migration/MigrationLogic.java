package views.migration;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Singleton;

import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import views.migration.components.MainPanel;

@Singleton
public class MigrationLogic {

	public enum ButtonTypes {
		PROJECTBUTTON, TABLEBUTTON
	}

	MainPanel mainPanel;

	Map<String, Object> projectButtonMap = new TreeMap<>();

	java.awt.Frame frame;

	public MigrationLogic(Composite swtAwtComponent) {
		mainPanel = new MainPanel(swtAwtComponent);
		createJframe(swtAwtComponent);
	}

	public void createJframe(Composite swtAwtComponent) {
		frame = SWT_AWT.new_Frame(swtAwtComponent);
		frame.setBackground(Color.yellow);
		frame.add(mainPanel);
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				Component c = (Component) evt.getSource();
				mainPanel.resizeElement(c.getHeight(), c.getWidth());
			}
		});

	}
}
