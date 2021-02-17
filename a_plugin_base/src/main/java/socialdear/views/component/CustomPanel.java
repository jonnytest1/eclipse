package socialdear.views.component;

import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import socialdear.exceptions.NoParentException;

public class CustomPanel extends JPanel {
	public enum DIRECTIONS {
		NORTH("North"), SOUTH("South"), WEST("West"), EAST("East");

		private final String text;

		DIRECTIONS(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private static final long serialVersionUID = 1L;

	private transient CustomDimension currentConstraints;

	public CustomDimension getCurrentConstraints() {
		return currentConstraints;
	}

	public void setCurrentPostion(CustomDimension currentConstraints) {
		this.currentConstraints = currentConstraints;
	}

	public void add(CustomDimension dim, CustomPanel component) {
		add(component);
		component.setPositionXY(dim);
		component.setCurrentPostion(dim);
	}

	public void add(CustomDimension pos, JComponent com) {
		add(com);
		ComponentUtil.setToParent(DIRECTIONS.NORTH, pos.getHeight(), com);
		ComponentUtil.setToParent(DIRECTIONS.WEST, pos.getWidth(), com);
	}

	public void addInReferenceToWithSize(JComponent component, CustomDimension position, JComponent com) {
		addInReferenceTo(component, position, com);
	}

	public void addInReferenceTo(JComponent component, CustomDimension position, JComponent com) {
		add(com);
		ComponentUtil.distanceBetween(DIRECTIONS.EAST, position.getWidth(), com, component);
		ComponentUtil.distanceBetween(DIRECTIONS.SOUTH, position.getHeight(), com, component);
	}

	public void add(CustomDimension pos, CustomDimension size, CustomPanel com) {
		if (pos == null) {
			pos = new CustomDimension();
		}
		add(pos, com);
		setHeight(com, (int) size.getHeight());
		setHeight(com, (int) size.getWidth());
	}

	public void add(CustomDimension pos, CustomPanel com, CustomDimension size) {
		if (pos == null) {
			pos = new CustomDimension();
		}
		if (size == null) {
			size = new CustomDimension();
		}
		add(pos, com);
		getSpringLayout().putConstraint(DIRECTIONS.SOUTH.text, com, (int) size.getHeight(), DIRECTIONS.SOUTH.text,
				com.getParent());
		getSpringLayout().putConstraint(DIRECTIONS.EAST.text, com, (int) size.getWidth(), DIRECTIONS.EAST.text,
				com.getParent());
		validate();
	}

	public SpringLayout getSpringLayout() {
		LayoutManager layout = getLayout();
		if (layout instanceof SpringLayout) {
			return (SpringLayout) layout;
		} else {
			layout = new SpringLayout();
			setLayout(layout);
			return (SpringLayout) layout;
		}
	}

	public void setToParent(DIRECTIONS springLayoutDirection, int distance) {
		CustomPanel parent = ((CustomPanel) getParent());

		if (parent == null) {
			throw new NoParentException("no parent for " + this.getClass().getName());
		}
		parent.getSpringLayout().putConstraint(springLayoutDirection.toString(), this, distance,
				springLayoutDirection.toString(), getParent());
		getParent().repaint();
	}

	public void resize() {
		SpringLayout layout = getSpringLayout();

		layout.putConstraint(SpringLayout.WEST, this, 0, SpringLayout.EAST, this.getParent());
		layout.putConstraint(SpringLayout.NORTH, this, 0, SpringLayout.NORTH, this.getParent());

	}

	public void setMaxHeight() {
		CustomPanel parent = (CustomPanel) getParent();
		parent.getSpringLayout();
		setToParent(DIRECTIONS.SOUTH, 0);
		setToParent(DIRECTIONS.NORTH, 0);
	}

	public static void setWidth(JComponent component, int width) {
		CustomPanel parent = (CustomPanel) component.getParent();
		SpringLayout layout = parent.getSpringLayout();
		layout.putConstraint(SpringLayout.WEST, component, width, SpringLayout.EAST, component);
	}

	public static void setHeight(JComponent component, double height) {
		setHeight(component, (int) height);
	}

	public static void setHeight(JComponent component, int height) {
		CustomPanel parent = (CustomPanel) component.getParent();
		SpringLayout layout = parent.getSpringLayout();
		layout.putConstraint(SpringLayout.NORTH, component, height, SpringLayout.SOUTH, component);
	}

	public void setPositionXY(CustomDimension currentConstraints) {
		setPositionXY(currentConstraints.getWidth(), currentConstraints.getHeight());

	}

	public void setPositionXY() {
		setPositionXY(getCurrentConstraints().getWidth(), getCurrentConstraints().getHeight());

	}

	public void setPositionXY(double width, double height) {
		setPositionXYInt((int) width, (int) height);
	}

	public void setPositionXYInt(int x, int y) {
		CustomPanel parent = (CustomPanel) this.getParent();
		setToParent(DIRECTIONS.WEST, x);
		setToParent(DIRECTIONS.NORTH, y);

		parent.revalidate();
		parent.repaint();
	}

}
