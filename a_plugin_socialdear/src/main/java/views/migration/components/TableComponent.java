package views.migration.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import socialdear.http.logging.Logging;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomDimension;
import socialdear.views.component.CustomPanel;
import sql.AlterTableCommand;
import sql.Column;
import sql.Constraint;
import sql.CreateTableCommand;

public class TableComponent extends CustomPanel implements ImplementedMouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private int calculatedWidth;

	private int calculatedHeight;
	private CustomDimension startPos;

	private String title;

	private Map<String, Object> props;

	private List<Column> columns;

	private SpringLayout layout;
	private List<Constraint> constraints;

	Map<String, Integer> columnPositions = new TreeMap<>();

	public TableComponent(String title, List<Column> columns, List<Constraint> constraints, Map<String, Object> props) {
		super();
		this.title = title;
		this.props = props;
		this.columns = columns;
		this.constraints = constraints;
		setBackground(Color.ORANGE);
		layout = new SpringLayout();
		this.setLayout(layout);
		recreate();
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public void recreate() {
		removeAll();
		calculatedHeight = 40;
		calculatedWidth = 100;
		JTextField jTitle = new JTextField(title);
		if (title.length() > 10) {
			calculatedWidth += (title.length() - 10) * 10;
		}
		jTitle.setEditable(true);
		layout.putConstraint(SpringLayout.NORTH, jTitle, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, jTitle, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, jTitle, -5, SpringLayout.EAST, this);
		add(jTitle);

		JTextArea jLine = new JTextArea();
		jLine.setPreferredSize(new Dimension(20, 10));
		jLine.setBackground(Color.BLACK);
		layout.putConstraint(SpringLayout.NORTH, jLine, 2, SpringLayout.SOUTH, jTitle);
		layout.putConstraint(SpringLayout.WEST, jLine, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, jLine, 0, SpringLayout.EAST, this);
		add(jLine);

		for (Column att : columns) {
			JTextArea jAtt = new JTextArea(att.getColumnName());
			if (att.isPrimaryKey()) {
				jAtt.setBackground(Color.cyan);
			}
			jAtt.addMouseListener(this);
			layout.putConstraint(SpringLayout.WEST, jAtt, 2, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, jAtt, calculatedHeight, SpringLayout.NORTH, this);
			columnPositions.put(att.getColumnName(), calculatedHeight);
			calculatedHeight += 22;
			jAtt.setEditable(true);
			add(jAtt);
		}

		layout.putConstraint(SpringLayout.WEST, this, calculatedWidth, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, this, calculatedHeight, SpringLayout.SOUTH, this);
		setPreferredSize(new Dimension(calculatedWidth, calculatedHeight));
	}

	public TableComponent(CreateTableCommand tableCommand, Map<String, Object> props) {
		this(tableCommand.getTableName(), new ArrayList<>(tableCommand.getColumns()),
				new ArrayList<>(tableCommand.getContraints()), props);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getCalculatedWidth() {
		return calculatedWidth;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JComponent component = (JComponent) e.getSource();
		if (component instanceof TableComponent) {

			startPos = new CustomDimension(e.getXOnScreen(), e.getYOnScreen());
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		startPos = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		CustomPanel component = (CustomPanel) e.getSource();
		if (component instanceof TableComponent) {
			TableComponent comp = (TableComponent) component;
			CustomDimension currentMousePosition = new CustomDimension(e.getXOnScreen(), e.getYOnScreen());
			CustomDimension difference = currentMousePosition.minus(startPos);

			comp.getCurrentConstraints().add(difference);

			setPositionXY();
			startPos = currentMousePosition;
			revalidate();
			repaint();
		}

	}

	public void alter(AlterTableCommand cmd) {
		switch (cmd.getType()) {
		case ADD:
			this.columns.add(cmd.getColumn());
			break;
		case ALTER:
			this.columns.removeIf((column) -> column.getColumnName().equals(cmd.getColumn().getColumnName()));
			this.columns.add(cmd.getColumn());
			break;
		case DROP:
			this.columns.removeIf((column) -> column.getColumnName().equals(cmd.getColumn().getColumnName()));
			break;
		case ADD_PRIMARY:
			// Column column = this.columns.stream().reduce(null, (previous,
			// current) -> current.getColumnName().equals(cmd.getColumn().getColumnName()) ?
			// current : previous);
			// column.setPrimaryKey(true);
			break;
		case ADD_CONSTRAINT:
			break;
		case CHANGE:
			break;
		default:
			Logging.sendToConsole("unimplemented AlterTableCommand " + cmd.getType().toString());
		}
		recreate();

	}

	public CustomDimension getStartPos() {
		return startPos;
	}

	public String getTitle() {
		return title;
	}

	public String getTableName() {
		return title;
	}

	public Map<String, Object> getProps() {
		return props;
	}

	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public SpringLayout getLayout() {
		return layout;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public Map<String, Integer> getColumnPositions() {
		return columnPositions;
	}

	public int getCalculatedHeight() {
		return calculatedHeight;
	}

	public int getOffset(String columnName) {
		return columnPositions.get(columnName) + 11;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
