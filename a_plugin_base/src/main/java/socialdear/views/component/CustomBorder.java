package socialdear.views.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

/**
 *  1.2
 */
public class CustomBorder extends AbstractBorder {

	public enum Shape {
		Circular, Rectangle;
	}

	/** 
	 * 
	 */
	private static final long serialVersionUID = 660731753777355454L;
	private Color color;
	private int thickness = 4;
	private int radii = 8;
	private int pointerSize = 7;
	private Insets insets = null;
	private transient BasicStroke stroke = null;
	private int strokePad;
	private int pointerPad = 4;
	private boolean left = true;
	transient RenderingHints hints;
	private Shape shape;

	public CustomBorder(Color color) {
		this(color, 4, 8, 7);
	}

	public CustomBorder(Color color, int thickness, int radii, int pointerSize) {

		this.thickness = thickness;
		this.radii = radii;
		this.pointerSize = pointerSize;
		this.color = color;

		stroke = new BasicStroke(thickness);
		strokePad = thickness / 2;

		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int pad = radii + strokePad;
		int bottomPad = pad + pointerSize + strokePad;
		insets = new Insets(pad, pad, bottomPad, pad);
	}

	public CustomBorder(Color color, int thickness, int radii, int pointerSize, boolean left) {
		this(color, thickness, radii, pointerSize);
		this.left = left;
	}

	public CustomBorder(Shape shape, int borderSize, int scale) {
		this(Color.black, borderSize, scale, 0);
		insets = new Insets(0, 0, 0, 0);
		pointerSize = scale;
		this.shape = shape;
		this.thickness = borderSize;

	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return getBorderInsets(c);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2 = (Graphics2D) g;

		int bottomLineY = height - thickness - pointerSize;

		java.awt.Shape bubble = null;

		switch (shape) {
		case Rectangle:
			bubble = new RoundRectangle2D.Double(0D + strokePad, 0D + strokePad, ((double) width) - thickness,
					bottomLineY, radii, radii);
			break;
		case Circular:
			bubble = new Ellipse2D.Double(0D + strokePad, 0D + strokePad, radii, radii);
			break;
		default:
			break;
		}

		Polygon pointer = new Polygon();

		if (left) {
			// left point
			pointer.addPoint(strokePad + radii + pointerPad, bottomLineY);
			// right point
			pointer.addPoint(strokePad + radii + pointerPad + pointerSize, bottomLineY);
			// bottom point
			pointer.addPoint(strokePad + radii + pointerPad + (pointerSize / 2), height - strokePad);
		} else {
			// left point
			pointer.addPoint(width - (strokePad + radii + pointerPad), bottomLineY);
			// right point
			pointer.addPoint(width - (strokePad + radii + pointerPad + pointerSize), bottomLineY);
			// bottom point
			pointer.addPoint(width - (strokePad + radii + pointerPad + (pointerSize / 2)), height - strokePad);
		}

		Area area = new Area(bubble);
		if (shape == Shape.Rectangle) {
			area.add(new Area(pointer));
		}
		g2.setRenderingHints(hints);

		// Paint the BG color of the parent, everywhere outside the clip
		// of the text bubble.
		Component parent = c.getParent();
		if (parent != null) {
			Color bg = parent.getBackground();
			Rectangle rect = new Rectangle(0, 0, width, height);
			Area borderRegion = new Area(rect);
			borderRegion.subtract(area);
			g2.setClip(borderRegion);
			g2.setColor(bg);
			g2.fillRect(0, 0, width, height);
			g2.setClip(null);
		}

		g2.setColor(color);
		g2.setStroke(stroke);
		g2.draw(area);
	}
}