package sql;

import java.util.ArrayList;
import java.util.List;

import exception.CustomInvalidClassException;
import exception.InvalidCreationException;
import exception.UnnecessarySqlCommandException;
import socialdear.http.logging.Logging;

public class Column {

	private static final String ALTER_TABLE = "ALTER TABLE `";

	private String columnSQL;
	private String columnName;
	boolean primaryKey = false;

	boolean isNullAllowed = true;

	boolean unsigned;

	String defaultValue;

	String type;

	private List<String> columnAttributes = new ArrayList<>();

	InvalidCreationException createdBy;

	private boolean nextCollate;

	private boolean nextNot;

	private boolean nextDefault;

	public Column(InvalidCreationException e) {
		createdBy = e;
	}

	Column(String title, Class<?> type) {
		this(title, type, false);

	}

	Column(String title, Class<?> type, boolean primary) {
		columnName = title;
		primaryKey = primary;
		this.type = type.getName();
		if ("java.lang.Long".equals(this.type)) {
			columnAttributes.add("BIGINT(20)");
			columnAttributes.add("UNSIGNED");
			columnAttributes.add("NOT NULL");
			columnAttributes.add("DEFAULT NULL");
		}
	}

	Column(String column) {
		columnSQL = column;

		String[] attMods = column.split(" ");

		if (columnSQL.contains("ADD PRIMARY KEY")) {
			columnName = attMods[attMods.length - 1].replaceAll("\\(", "").replaceAll("\\)", "");
			return;
		}
		nextDefault = false;
		nextNot = false;
		nextCollate = false;

		for (String mod : attMods) {
			parseMod(mod);
		}

	}

	private void parseMod(String mod) {
		int beginName = mod.indexOf('`');
		if (beginName > -1) {
			columnName = mod.replaceAll("`", "");
		} else {
			if (isNext(mod)) {
				return;
			}
			if (nextDefault) {
				columnAttributes.add("DEFAULT " + mod);
				nextDefault = false;
			}
			if (nextCollate) {
				nextCollate = false;
			} else if (nextNot) {
				columnAttributes.add("NOT " + mod);
				nextNot = false;
			} else {
				columnAttributes.add(mod);
			}

		}
	}

	private boolean isNext(String mod) {
		if (mod.equals("DEFAULT")) {
			nextDefault = true;
			return true;
		} else if (mod.equals("NOT")) {
			nextNot = true;
			return true;

		} else if (mod.equals("COLLATE")) {
			nextCollate = true;
			return true;

		}
		return false;
	}

	public String getColumnSQLCreateTablePart() {
		return columnSQL;
	}

	public void setColumnSQL(String columnSQL) {
		this.columnSQL = columnSQL;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setColumnAttributes(List<String> columnAttributes) {
		this.columnAttributes = columnAttributes;
	}

	@Override
	public String toString() {
		return columnName + " [ " + columnAttributes + " ] ";

	}

	public String javaToSQLType(String javaClass) throws CustomInvalidClassException {
		switch (javaClass.replace("class ", "").replace("interface ", "")) {
		case "java.lang.Long":
			return "BIGINT(20)";
		case "java.lang.String":
			return "VARCHAR(255)";
		case "java.time.Instant":
			return "DATETIME";
		case "boolean":
			return "TINYINT";
		case "long":
			return "BIGINT";
		case "int":
			return "INT";
		case "BLOB":
			return "BLOB";
		default:
			throw new CustomInvalidClassException("no sql type for " + javaClass + " defined", createdBy);
		}
	}

	public String getSQLTypeFromModel() {
		try {
			return javaToSQLType(getType());
		} catch (CustomInvalidClassException e) {
			Logging.sendToConsole(e.getMessage() + "\n" + (columnSQL == null ? "no sql" : columnSQL), e);
			return null;
		}
	}

	String getBaseType() {
		String sqlType = getSQLTypeFromModel();
		return sqlType.split("\\(")[0];
	}

	public String getSQLNullString() {
		if (isNullAllowed) {
			return "NULL";
		} else {
			return "NOT NULL";
		}
	}

	public String toSQL() throws UnnecessarySqlCommandException, CustomInvalidClassException {
		String sqlType;
		try {
			sqlType = javaToSQLType(getType());
		} catch (CustomInvalidClassException e) {
			Logging.sendToConsole(e.getMessage() + "\n" + (columnSQL == null ? "no column sql" : columnSQL), e);
			throw e;
		}
		if (sqlType == null) {
			throw new UnnecessarySqlCommandException();
		}
		String defaultVal = "NULL";
		return " `" + columnName.toUpperCase() + "` " + sqlType.toUpperCase() + " " + getSQLNullString() + " DEFAULT "
				+ defaultVal.toUpperCase();
	}

	public String getADDQSL(String tableName) throws UnnecessarySqlCommandException, CustomInvalidClassException {
		return ALTER_TABLE + tableName + "` \nADD" + toSQL() + ";\n";
	}

	public String getALTERSQL(String javaTableName) throws UnnecessarySqlCommandException, CustomInvalidClassException {
		return ALTER_TABLE + javaTableName + "` \nALTER COLUMN" + toSQL() + " ;\n";
	}

	public String getRemoveSQL(String javaTableName) {
		return ALTER_TABLE + javaTableName + "` \nDROP COLUMN `" + columnName.toUpperCase() + "`" + " ;\n";
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getColumnName() {
		return columnName;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public List<String> getColumnAttributes() {
		return columnAttributes;
	}

	public boolean isNullAllowed() {
		return isNullAllowed;
	}

	public void setNullAllowed(boolean isNullAllowed) {
		this.isNullAllowed = isNullAllowed;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean fitsAttribute(String sqlColumnAttribute) {
		String baseType = getBaseType();
		sqlColumnAttribute = sqlColumnAttribute.replace("TEXT", "VARCHAR").split("\\(")[0];

		return sqlColumnAttribute.contains(baseType);
	}
}
