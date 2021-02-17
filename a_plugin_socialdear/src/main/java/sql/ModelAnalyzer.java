package sql;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import exception.CustomInvalidClassException;
import exception.UnnecessarySqlCommandException;
import model.TableModel;
import model.TableModel.ClassAnnotations;
import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.logging.SystemProperties;
import util.ProjectFileParser;
import views.migration.components.DatabaseComponent;
import views.migration.components.TableComponent;
import views.migration.components.sidebar.SideBarComponent;

public class ModelAnalyzer {

	PrintWriter file = null;

	private List<TableModel> tables = new ArrayList<>();
	private IProject project;
	private SideBarComponent sideBarComponent;

	public ModelAnalyzer(IProject project, SideBarComponent sideBarComponent) {

		this.project = project;
		this.sideBarComponent = sideBarComponent;

		List<Class<?>> models = ProjectFileParser.getModelsAsClass(project);
		models.forEach(m -> {
			for (Annotation annotation : m.getDeclaredAnnotations()) {
				try {
					ClassAnnotations anno = TableModel.getEnumFromClass(annotation.annotationType(),
							ClassAnnotations.class);
					if (anno.equals(ClassAnnotations.ENTITY)) {
						tables.add(parseToTable(m));
						break;
					}
				} catch (IllegalArgumentException e) {
					//
				}
			}
		});
		updateReferences();

	}

	private void updateReferences() {
		tables.forEach(table -> {
			if (!table.getConstraints().isEmpty()) {
				for (Constraint constraint : table.getConstraints()) {
					for (TableModel tableObject : tables) {
						if (constraint.getReferencedTable().equals(tableObject.getTitle())) {
							tableObject.getColumns().add(new Column(constraint.referencedColumn, Long.class, false));
						}
					}
				}

			}
		});
	}

	private <T> TableModel parseToTable(Class<T> tableClass) {

		try {
			return new TableModel(tableClass);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
		return null;
	}

	public void createSqlForMigrate(DatabaseComponent databaseComponent, String string) {
		List<TableComponent> sqlTables = databaseComponent.getTables();
		StringBuilder sqlCommand = new StringBuilder();
		for (TableModel model : tables) {
			String sql = findTable(model, sqlTables);
			if (sql != null) {
				sqlCommand.append(sql);
				sqlCommand.append("\n\n");
			}
		}
		String sqlCommandString = sqlCommand.toString();
		Logging.sendToConsole(sqlCommandString);
		if (!sqlCommandString.isEmpty()
				&& Logging.showMessageOption("should create SQL for table", sqlCommandString, sideBarComponent)) {
			getMigrationFile(string);
			file.println(sqlCommandString);
		}
		if (file != null) {
			file.close();
		} else {
			Logging.showMessage("no changes necessary", "no CHANGE necessary");
		}
		sideBarComponent.run();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			SystemProperties.printInfo(e);
		}
	}

	public String findTable(TableModel javaTable, List<TableComponent> sqlTables) {
		for (TableComponent sqlTable : sqlTables) {
			if (javaTable.getTitle().equalsIgnoreCase(sqlTable.getTableName())) {
				return evaluateTable(javaTable, sqlTable);
			}
		}
		return javaTable.createSql();
	}

	private String evaluateTable(TableModel javaTable, TableComponent sqlTable) {
		StringBuilder sql = new StringBuilder();
		for (Column javaColumn : javaTable.getColumns()) {
			String columnSQL = findColumn(javaColumn, sqlTable.getColumns(), javaTable.getTitle());
			if (columnSQL != null) {
				sql.append(columnSQL);
			}
		}
		for (Column sqlColumn : sqlTable.getColumns()) {
			if (javaTable.getColumns().stream()
					.noneMatch(column -> sqlColumn.getColumnName().equalsIgnoreCase(column.getColumnName()))) {
				sql.append(sqlColumn.getRemoveSQL(sqlTable.getTitle()));
			}
		}
		return sql.toString().equals("") ? null : sql.toString();
	}

	private String findColumn(Column javaColumn, List<Column> sqlColumns, String javaTableName) {
		for (Column sqlColumn : sqlColumns) {
			if (javaColumn.getColumnName().equalsIgnoreCase(sqlColumn.getColumnName())) {
				return matchColumnAttributes(javaColumn, sqlColumn, javaTableName);
			}
		}
		try {
			return javaColumn.getADDQSL(javaTableName);
		} catch (UnnecessarySqlCommandException | CustomInvalidClassException e) {
			return null;
		}
	}

	private String matchColumnAttributes(Column javaColumn, Column sqlColumn, String javaTableName) {
		StringBuilder sql = new StringBuilder();

		for (String sqlColumnAttribute : sqlColumn.getColumnAttributes()) {
			if (needsAlterSQL(sqlColumnAttribute, javaColumn)) {
				try {
					sql.append(javaColumn.getALTERSQL(javaTableName));
					sql.append("\n");
				} catch (UnnecessarySqlCommandException | CustomInvalidClassException e) {
					// skip this one
				}
			}
		}
		String sqlString = sql.toString();
		return "".equals(sqlString) ? null : sqlString;
	}

	boolean needsAlterSQL(String sqlColumnAttribute, Column javaColumn) {
		if (sqlColumnAttribute.contains("DEFAULT") || sqlColumnAttribute.contains("UNSIGNED")) {
			return false;
		} else if (sqlColumnAttribute.contains("NULL")) {
			return false;

		} else if (sqlColumnAttribute.contains("AUTO_INCREMENT")) {
			return !javaColumn.primaryKey;
		} else {
			if (javaColumn.getType().contains("model.")) {
				return false;
			}
			return javaColumn.getSQLTypeFromModel() != null && !javaColumn.fitsAttribute(sqlColumnAttribute);
		}
	}

	private void getMigrationFile(String name) {
		if (file == null) {
			if (!name.endsWith(".sql")) {
				name += ".sql";
			}
			IFolder folder = project.getFolder("src/main/resources/db/migration");
			try {
				String path = MessageFormat.format("{0}/{1}", folder.getRawLocationURI(), name);
				file = new PrintWriter(path.replace("file:/", ""), "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				SystemProperties.printInfo(e);
			}
		}
	}

	public boolean hasChanges(DatabaseComponent databaseComponent) {
		List<TableComponent> sqlTables = databaseComponent.getTables();
		StringBuilder sqlCommand = new StringBuilder();
		for (TableModel model : tables) {
			String sql = findTable(model, sqlTables);
			if (sql != null) {
				sqlCommand.append(sql);
				sqlCommand.append("\n\n");
			}
		}
		return sqlCommand.length() != 0;
	}
}
