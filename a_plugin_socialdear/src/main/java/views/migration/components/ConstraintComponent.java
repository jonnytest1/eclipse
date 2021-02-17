package views.migration.components;

import java.awt.geom.Line2D;

import socialdear.views.component.CustomDimension;
import socialdear.views.component.CustomPanel;
import sql.Constraint;

public class ConstraintComponent extends CustomPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private transient Constraint constraint;

	private TableComponent table;

	private TableComponent targetTable;

	public ConstraintComponent(Constraint constraint, TableComponent table, TableComponent targetTable) {
		this.constraint = constraint;
		this.table = table;
		this.targetTable = targetTable;

	}

	public void recreate() {
		removeAll();
		repaint();

	}

	public Line2D getLine() {
		CustomDimension startTable = table.getCurrentConstraints();
		CustomDimension startColumn = startTable
				.plus(new CustomDimension(table.getCalculatedWidth(), table.getOffset(constraint.getColumn())));

		CustomDimension endColumn = targetTable.getCurrentConstraints()
				.plus(new CustomDimension(-2, targetTable.getOffset(constraint.getReferencedColumn())));
		return new Line2D.Float(startColumn.toPoint2D(), endColumn.toPoint2D());
	}

}
