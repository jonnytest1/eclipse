package views.logging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import model.launcher.ELKLoggingModel;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

public class SingleLoggingElementPanel extends CustomElementPanel implements ImplementedMouseListener {

	/**
	 * 
	 */
	private boolean isClicked = false;

	private static final long serialVersionUID = 2035899710428763957L;

	private ELKLoggingModel logEntry;

	private Dimension dimension;

	private boolean isHighlighted;

	public SingleLoggingElementPanel(ELKLoggingModel logEntry, Dimension dimension, boolean isHighlighted) {

		logEntry.setReferencingJComponent(this);
		this.dimension = dimension;
		this.isHighlighted = isHighlighted;
		setOpaque(false);
		setVisible(true);
		addMouseListener(this);
		this.logEntry = logEntry;
		addElements();
	}

	private double getHTMLWidth() {
		return dimension.getWidth() / 1.4;

	}

	@Override
	protected void addElements() {

		JLabel comp;
		if (isClicked()) {
			comp = new JLabel("<html><p style=\"width:" + getHTMLWidth() + "px\">"
					+ logEntry.toExpandedString().replaceAll("\n", "<br>") + "</p></html>");

		} else {
			comp = new JLabel("<html><p style=\"width:" + getHTMLWidth() + "px\">"
					+ logEntry.toShortString((int) getHTMLWidth()).replaceAll("\n", "<br>") + "</p></html>");
		}
		if (isHighlighted) {
			comp.setBackground(Color.LIGHT_GRAY);
			comp.setOpaque(true);
		}
		add(comp);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setClicked(!isClicked());
		recreate();

	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}

}
