package socialdear.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

public class JPanelProvider {

	private JPanelProvider() {
		// private
	}

	/**
	 * @since 3.0
	 */
	public static Composite addJPanel(JPanel panel, Composite rootComposite) {
		panel.setBounds(0, 0, 450, 300);
		Composite intermediateComposite = new Composite(rootComposite, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(intermediateComposite);
		frame.setFocusable(true);
		frame.setFocusableWindowState(true);
		frame.setBackground(Color.yellow);
		frame.add(panel);
		frame.setSize(new Dimension(rootComposite.getSize().x, rootComposite.getSize().y));
		frame.setMaximizedBounds(new Rectangle(0, 0, rootComposite.getSize().x, rootComposite.getSize().y));
		panel.setMaximumSize(new Dimension(rootComposite.getSize().x, rootComposite.getSize().y));
		panel.setSize(new Dimension(rootComposite.getSize().x, rootComposite.getSize().y));
		panel.setBounds(new Rectangle(0, 0, rootComposite.getSize().x, rootComposite.getSize().y));
		panel.setPreferredSize(new Dimension(rootComposite.getSize().x, rootComposite.getSize().y));

		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent evt) {
				// Component c = (Component) evt.getSource();
				// panel.resizeElement(c.getHeight(), c.getWidth());
			}
		});

		return intermediateComposite;
	}

}
