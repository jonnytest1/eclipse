package sql;

import views.migration.components.sidebar.SideBarSQLCommandComponent;

public class SQLCommand {

	private String sql;
	private boolean parsable;

	private SideBarSQLCommandComponent selectionElement;

	public void setParsable(boolean parsable) {
		this.parsable = parsable;
		if (selectionElement != null) {
			selectionElement.setParsable(parsable);
			if (parsable) {
				selectionElement.setSuccessful();
			} else {
				selectionElement.setFailed();
			}
		}
	}

	private String fileName;
	private boolean enabled;

	public SQLCommand(String sql, boolean parsable, String fileName) {
		this(sql, fileName);
		this.parsable = parsable;

	}

	public String getShortForm() {
		return sql.split("\n")[0];
	}

	public SQLCommand(String sql, String fileName) {
		this.sql = sql;
		this.fileName = fileName;
		this.parsable = true;
		enabled = true;
	}

	public static SQLCommand parse(String sql, String fileName) {
		if (sql.startsWith("CREATE TABLE")) {
			return new CreateTableCommand(sql, fileName);
		} else if (sql.startsWith("ALTER TABLE")) {
			return new AlterTableCommand(sql, fileName);
		} else if (sql.startsWith("INSERT INTO ") || (sql.startsWith("UPDATE ") && sql.contains("SET"))) {
			return new NoChangeCommand(sql, fileName);
		} else {
			return new SQLCommand(sql, false, fileName);
		}
	}

	public String getSql() {
		return sql;
	}

	public boolean isParsable() {
		return parsable;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public SideBarSQLCommandComponent getSelectionElement() {
		return selectionElement;
	}

	public void setSelectionElement(SideBarSQLCommandComponent selectionElement) {
		this.selectionElement = selectionElement;
	}

	@Override
	public String toString() {

		return sql + ";";
	}

}
