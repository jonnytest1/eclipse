package lib;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class JFrameTest {

	protected JFrame frame;

	@BeforeEach
	public void createFrame() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());

		frame.setVisible(true);
		frame.setSize(1000, 900);

	}

	@AfterEach
	public void waitClosed() {
		frame.validate();
		frame.repaint();
		while (frame.isActive()) {
			// wait for closing
		}
	}

}
