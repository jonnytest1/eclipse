package views.migration.components.sidebar;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.util.files.FileParser;
import socialdear.util.files.ResourceMonitor;
import socialdear.views.component.CustomElementPanel;
import sql.ModelAnalyzer;
import sql.SQLCommand;
import views.migration.components.DatabaseComponent;

public class SideBarComponent extends CustomElementPanel implements Runnable {

	public enum Type {
		Project, File, Command, Enabler
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<IProject> projects;

	private IProject currentProject;

	public static final int sidebar_width = 200;

	private ModelAnalyzer analyzer;

	private DatabaseComponent databaseComponent;

	private List<SQLCommand> commands;

	static boolean first = true;

	public SideBarComponent() {
		this.databaseComponent = new DatabaseComponent(null);
		this.projects = FileParser.getProjects();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setAlignmentY(TOP_ALIGNMENT);

		ResourceMonitor.registerResourceChangeListener(new IResourceDeltaVisitor() {

			boolean isEntityClass(IResource res) {
				try {
					IFile file = (IFile) res;
					if (file.getName().endsWith(".java")) {
						return new BufferedReader(new InputStreamReader(file.getContents())).lines()
								.anyMatch(line -> line.contains("@Entity"));

					}
					return false;
				} catch (ClassCastException | CoreException e) {
					return false;
				}
			}

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource res = delta.getResource();
				if (res.getType() == IResource.FILE) {
					if (res.getFullPath().toString().contains("migration") || isEntityClass(res)) {
						run();
					}
					return false;
				}

				return true;

			}
		});

		recreate();

	}

	@Override
	public void run() {
		if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			executor.schedule(this, 20, TimeUnit.MILLISECONDS);
		} else {
			loadFromSQL();
			recreate();
		}

	}

	public void loadFromSQL() {
		if (currentProject != null) {
			analyzer = new ModelAnalyzer(currentProject, this);
			Logging.logRequest("loaded from SQL and models", LogLevel.INFO, null, null);
		}
	}

	@Override
	protected void addElements() {

		setBackground(Color.GREEN);

		for (IProject project : projects) {
			SidebarProjectComponent projectComponent = new SidebarProjectComponent(project, this,
					project == currentProject || first);
			first = false;
			add(projectComponent);
		}
	}

	public void rebuildDataBaseView() {
		databaseComponent.recreate();
	}

	public IProject getProject() {
		return currentProject;
	}

	@Override
	public int getWidth() {
		return sidebar_width;
	}

	public void setCommands(IProject project, List<SQLCommand> sqlCommands) {
		if (project == currentProject) {
			currentProject = null;
		} else {
			this.currentProject = project;
			commands = sqlCommands;
			databaseComponent.setCommands(project, sqlCommands);
			loadFromSQL();
			analyzer.hasChanges(databaseComponent);

		}

		// contentPane.clearLines();

	}

	public List<SQLCommand> getCommands() {
		return commands;
	}

	public DatabaseComponent getDatabaseComponent() {
		return databaseComponent;
	}

	@Override
	public void resizeElement(int height, int width) {
		databaseComponent.resizeElement(height - width, width);
	}

	public void createSQL(String text) {
		analyzer.createSqlForMigrate(databaseComponent, text);
	}
}
