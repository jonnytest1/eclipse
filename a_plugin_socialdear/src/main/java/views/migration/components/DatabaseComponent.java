package views.migration.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.SpringLayout;

import org.eclipse.core.resources.IProject;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.views.component.ComponentUtil;
import socialdear.views.component.CustomDimension;
import socialdear.views.component.CustomElementPanel;
import sql.AlterTableCommand;
import sql.CreateTableCommand;
import sql.NoChangeCommand;
import sql.SQLCommand;
import views.migration.MigrationLogic.ButtonTypes;
import views.migration.components.sidebar.SideBarComponent;

public class DatabaseComponent extends CustomElementPanel {

	private static final long serialVersionUID = 1L;

	private SideBarComponent sidebar;

	private transient IProject project;

	private Map<String, TableComponent> tables;
	private transient List<SQLCommand> sqlCommands;

	private List<ConstraintComponent> lines = new ArrayList<>();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public SideBarComponent getSidebar() {
		return sidebar;
	}

	public List<TableComponent> getTables() {
		return new ArrayList<>(tables.values());
	}

	public DatabaseComponent(IProject project) {
		this.project = project;
		this.tables = new TreeMap<>();
		// layout
		SpringLayout mgr = new SpringLayout();
		setLayout(mgr);

		// sidebar
		setBackground(Color.CYAN);
		addElements();
	}

	public void setCommands(IProject project, List<SQLCommand> sqlCommands) {
		this.sqlCommands = sqlCommands;
		this.project = project;
		recreate();

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		for (ConstraintComponent line : lines) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.draw(line.getLine());
		}
	}

	public void addLine(ConstraintComponent line) {
		lines.add(line);
	}

	public void clearLines() {
		lines = new ArrayList<>();
	}

	@Override
	protected void addElements() {
		int offset = 10;
		int verticalOffset = 10;
		int highestTable = -1;

		tables = new HashMap<>();
		clearLines();
		if (sqlCommands != null && getElementSize() != null) {
			for (SQLCommand sqlCommand : sqlCommands) {
				if (sqlCommand.isEnabled()) {
					Map<String, Object> props = new TreeMap<>();
					props.put("type", ButtonTypes.TABLEBUTTON);
					props.put("project", project);
					props.put("file", sqlCommand.getFileName());
					if (sqlCommand instanceof CreateTableCommand) {
						TableComponent table = new TableComponent((CreateTableCommand) sqlCommand, props);
						if (offset > getElementSize().getWidth()) {
							offset = 10;
							verticalOffset += highestTable + 10;
							highestTable = -1;
						}
						add(new CustomDimension(offset, verticalOffset), table);
						tables.put(table.getTableName(), table);
						offset += table.getCalculatedWidth() + 10;
						highestTable = Math.max(highestTable, table.getCalculatedHeight());
						((CreateTableCommand) sqlCommand).getContraints().forEach(constraint -> {
							ConstraintComponent constraintCp = new ConstraintComponent(constraint, table,
									tables.get(constraint.getReferencedTable()));

							addLine(constraintCp);
							add(constraintCp);
						});
					} else if (sqlCommand instanceof AlterTableCommand) {
						try {
							AlterTableCommand cmd = (AlterTableCommand) sqlCommand;
							cmd.setParsable(true);
							TableComponent table = tables.get(cmd.getTableName());
							if (table == null) {
								cmd.setParsable(false);
							} else {
								table.alter(cmd);
							}
						} catch (NullPointerException e) {
							String error = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString)
									.collect(Collectors.joining("\n"));
							Logging.logRequest("\ncouldnbt get type from sql Nullpointer Exception\n"
									+ sqlCommand.getSql() + error, LogLevel.INFO, null, e);

						}
					} else if (sqlCommand instanceof NoChangeCommand) {
						// jsut movind dataSet around in here ?
					} else {
						Logging.logRequest("\"unknown sql type\"\n" + sqlCommand.getSql(), LogLevel.INFO, null, null);
					}
				}

			}
		}
	}

	public void rebuild(IProject project) {
		removeAll();
		this.project = project;
		clearLines();
		addElements();
		validate();
		repaint();
	}

	public IProject getProject() {
		return this.project;
	}

	public void distanceTo(DIRECTIONS dir, int i, JComponent component) {
		getSpringLayout().putConstraint(dir.toString(), this, i, ComponentUtil.getOpposing(dir).toString(), component);

	}
}
