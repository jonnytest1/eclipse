package socialdear.views.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import socialdear.listeners.ImplementedComponentListener;

public class ListViewComponent extends CustomElementPanel implements ImplementedComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5337684935203307791L;

	private GridBagConstraints c;

	private int pos = 0;

	private JPanel comp;

	protected ListViewComponent() {
		setLayout(new GridBagLayout());
		addComponentListener(this);
		c = new GridBagConstraints();

		c.gridx = 1;
		comp = new JPanel();
	}

	@Override
	public Component add(Component e) {
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = pos++;
		c.weighty = 1;
		c.weightx = 1;
		add(e, c);
		addResizePanel();
		return e;
	}

	void addResizePanel() {
		remove(comp);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1000000;
		c.weightx = 1;
		c.gridy = pos + 1;
		add(comp, c);
	}

	@Override
	protected void addElements() {

		// nothin
	}

	@Override
	public void componentResized(ComponentEvent e) {
		revalidate();
		repaint();

	}
}
