package socialdear.views.component;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 *  1.2
 */
public class CustomDimension {

	double x;
	double y;

	public CustomDimension(int x, int y) {
		this((double) x, (double) y);
	}

	public CustomDimension(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public CustomDimension() {
		this.x = 0;
		this.y = 0;
	}

	public CustomDimension(Dimension size) {
		this.x = size.getWidth();
		this.y = size.getHeight();
	}

	public CustomDimension plus(CustomDimension startPos) {
		return plus(startPos.getWidth(), startPos.getHeight());

	}

	public CustomDimension plus(double x, double y) {
		return new CustomDimension(this.x + x, this.y + y);
	}

	public CustomDimension plus(int x, int y) {
		return plus((double) x, (double) y);
	}

	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public void add(CustomDimension startPos) {
		add(startPos.getWidth(), startPos.getHeight());

	}

	public CustomDimension minus(int x, int y) {
		return minus((double) x, (double) y);
	}

	public CustomDimension minus(double x, double y) {
		return new CustomDimension(this.x - x, this.y - y);
	}

	public CustomDimension minus(CustomDimension startPos) {
		return minus(startPos.getWidth(), startPos.getHeight());
	}

	public double getWidth() {
		return x;
	}

	public double getHeight() {
		return y;
	}

	@Override
	public String toString() {
		return "x: " + x + " y: " + y;
	}

	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}
}
