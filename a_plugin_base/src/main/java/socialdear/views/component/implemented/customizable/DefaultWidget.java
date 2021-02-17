package socialdear.views.component.implemented.customizable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomIcon;

public class DefaultWidget extends CustomizableViewPanel implements ImplementedMouseListener {

	private static final long serialVersionUID = 4789844181271665053L;

	DefaultWidget() {

		setLayout(new BorderLayout());
		setOpaque(false);
		addMouseListener(this);
		recreate();
	}

	@Override
	protected void addElements() {
		JLabel comp = new JLabel(new CustomIcon("add.png", this));
		comp.setAlignmentY((float) 0.5);
		add(comp, BorderLayout.CENTER);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setBackground(Color.LIGHT_GRAY);
		setOpaque(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setBackground(null);
		setOpaque(false);
	}

}
