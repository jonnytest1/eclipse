package sql;

import java.util.ArrayList;
import java.util.List;

import socialdear.logging.SystemProperties;

public class CreateTableCommand extends SQLCommand {

	String tableName;
	String primaryKey;
	List<Column> columns = new ArrayList<>();
	List<Constraint> contraints = new ArrayList<>();

	public List<Column> getColumns() {
		return columns;
	}

	public List<Constraint> getContraints() {
		return contraints;
	}

	public CreateTableCommand(String sql, String fileName) {
		super(sql, fileName);
		tableName = sql.split("CREATE TABLE ")[1].split(" \\(")[0].replaceAll("`", "").replace("IF NOT EXISTS ", "");

		int beginAttributes = sql.indexOf('(');
		int endAttributes = sql.lastIndexOf(')');
		String[] attributes = sql.substring(beginAttributes + 1, endAttributes).split(",");
		for (String att : attributes) {
			try {
				att = att.trim();
				if (att.contains("PRIMARY KEY")) {
					this.primaryKey = att.split("\\(")[1].split("\\)")[0].replaceAll("`", "");
					columns.forEach(column -> {
						if (column.getColumnName().equals(primaryKey)) {
							column.setPrimaryKey(true);
						}
					});
				} else if (att.contains("CONSTRAINT")) {
					contraints.add(new Constraint(att));
				} else {
					columns.add(new Column(att));
				}

			} catch (Exception e) {
				SystemProperties.printInfo(e);
			}

		}
	}

	@Override
	public String getShortForm() {
		return "CREATE TABLE " + tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

}
