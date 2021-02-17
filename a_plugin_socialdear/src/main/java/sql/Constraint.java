package sql;

public class Constraint {

	String constraintName;

	String ownColumn;
	String referencedTable;
	String referencedColumn;

	public String getName() {
		return constraintName;
	}

	public String getColumn() {
		return ownColumn;
	}

	public String getReferencedTable() {
		return referencedTable;
	}

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public Constraint(String constraintName, String ownColumn, String referencingTable, String referencingColumn) {
		this.constraintName = constraintName;
		this.ownColumn = ownColumn;
		referencedTable = referencingTable;
		referencedColumn = referencingColumn;

	}

	public Constraint(String att) {
		constraintName = att.split("CONSTRAINT `")[1].split("` FOREIGN KEY")[0];
		ownColumn = att.split("FOREIGN KEY \\(`")[1].split("`")[0];
		String[] references = att.split("REFERENCES `")[1].split("` ");
		referencedTable = references[0];
		referencedColumn = references[1].split("\\(`")[1].split("`\\)")[0];
	}

	public Constraint() {
		// constructor
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public void setOwnColumn(String ownColumn) {
		this.ownColumn = ownColumn;
	}

	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

}
