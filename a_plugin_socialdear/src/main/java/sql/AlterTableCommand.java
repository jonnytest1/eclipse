package sql;

import socialdear.logging.SystemProperties;

public class AlterTableCommand extends SQLCommand {

	public enum ColumnAction {
		DROP, ADD, ALTER, ADD_PRIMARY, ADD_CONSTRAINT, CHANGE
	}

	ColumnAction type;

	public ColumnAction getType() {
		return type;
	}

	public String getTable() {
		return table;
	}

	public Column getColumn() {
		return column;
	}

	String table;

	Column column;

	public AlterTableCommand(String sql, String fileName) {
		super(sql, fileName);
		sql = sql.replace("UPDATE ", "ALTER TABLE ").replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("  ", " ")
				.replaceAll("  ", " ");

		String[] tableSplit = sql.split("ALTER TABLE ")[1].replace("ADD PRIMARY", "ADDPRIMARY")
				.replace("ADD CONSTRAINT", "ADDCONSTRAINT").split(" ");
		table = tableSplit[0].trim();

		String sqlCommandType = tableSplit[1].split(" ")[0];
		try {
			this.type = Enum.valueOf(ColumnAction.class, sqlCommandType.toLowerCase().replace("modify", "ALTER"));

			switch (this.type) {
			case DROP:
				column = new Column(sql.split("DROP COLUMN ")[1].trim());
				break;
			case ALTER:
				column = new Column(sql.replace("ALTER COLUMN", "MODIFY COLUMN").split("MODIFY COLUMN ")[1].trim());
				break;
			case ADD:
				column = new Column(sql.split("ADD ")[1].trim());
				break;
			case ADD_PRIMARY:
				column = new Column(sql);
				break;
			case ADD_CONSTRAINT:

				break;
			case CHANGE:

				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			SystemProperties.printInfo(e);
		}
	}

	public String getTableName() {
		return this.table.replaceAll("`", "");
	}

}
