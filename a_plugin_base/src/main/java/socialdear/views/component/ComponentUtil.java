package socialdear.views.component;

import javax.swing.JComponent;

import socialdear.views.component.CustomPanel.DIRECTIONS;

/**
 * 1.2
 */
public class ComponentUtil {
	private ComponentUtil() {
		// private
	}

	public static void setToParent(DIRECTIONS SpringLayoutDirection, double distance, JComponent comp) {
		setToParent(SpringLayoutDirection, (int) distance, comp);
	}

	public static void setToParent(DIRECTIONS SpringLayoutDirection, int distance, JComponent comp) {
		if (SpringLayoutDirection == DIRECTIONS.EAST || SpringLayoutDirection == DIRECTIONS.SOUTH) {
			distance *= -1;
		}

		((CustomPanel) comp.getParent()).getSpringLayout().putConstraint(SpringLayoutDirection.toString(), comp,
				distance, SpringLayoutDirection.toString(), comp.getParent());
	}

	public static DIRECTIONS getOpposing(DIRECTIONS springLayoutDirection) {
		DIRECTIONS other = null;
		switch (springLayoutDirection) {
		case NORTH:
			other = DIRECTIONS.SOUTH;
			break;
		case SOUTH:
			other = DIRECTIONS.NORTH;
			break;
		case EAST:
			other = DIRECTIONS.WEST;
			break;
		case WEST:
			other = DIRECTIONS.EAST;
			break;

		default:
			break;
		}
		return other;
	}

	public static void level(DIRECTIONS direction, double distance, JComponent component, JComponent otherComponent) {

		level(direction, (int) distance, component, otherComponent);
	}

	public static void level(DIRECTIONS direction, int distance, JComponent component, JComponent otherComponent) {
		CustomPanel parent = ((CustomPanel) component.getParent());
		if (parent == null) {
			throw new RuntimeException("parent is null");
		}
		parent.getSpringLayout().putConstraint(direction.toString(), otherComponent, distance, direction.toString(),
				component);
		component.getParent().repaint();
	}

	public static void distanceBetween(DIRECTIONS direction, double distance, JComponent component,
			JComponent otherComponent) {
		distanceBetween(direction, (int) distance, component, otherComponent);

	}

	public static void distanceBetween(DIRECTIONS SpringLayoutDirection, int distance, JComponent comp,
			JComponent second) {
		DIRECTIONS other = getOpposing(SpringLayoutDirection);
		CustomPanel parent = ((CustomPanel) comp.getParent());
		if (parent == null) {
			throw new RuntimeException("parent is null");
		}
		parent.getSpringLayout().putConstraint(SpringLayoutDirection.toString(), comp, distance, other.toString(),
				second);
		comp.getParent().repaint();
	}

	public static void setHeight(double distance, JComponent comp) {
		setHeight((int) distance, comp);
	}

	public static void setHeight(int distance, JComponent comp) {
		((CustomPanel) comp.getParent()).getSpringLayout().putConstraint(DIRECTIONS.NORTH.toString(), comp, distance,
				DIRECTIONS.SOUTH.toString(), comp);
	}

	public static void setWidth(int distance, JComponent comp) {
		((CustomPanel) comp.getParent()).getSpringLayout().putConstraint(DIRECTIONS.WEST.toString(), comp, distance,
				DIRECTIONS.EAST.toString(), comp);
	}
}
