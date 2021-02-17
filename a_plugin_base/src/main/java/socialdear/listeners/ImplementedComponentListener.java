package socialdear.listeners;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 *  1.2
 */
public interface ImplementedComponentListener extends ComponentListener {

	@Override
	public default void componentShown(ComponentEvent e) {

	}

	@Override
	public default void componentResized(ComponentEvent e) {

	}

	@Override
	public default void componentMoved(ComponentEvent e) {

	}

	@Override
	public default void componentHidden(ComponentEvent e) {

	}
}
