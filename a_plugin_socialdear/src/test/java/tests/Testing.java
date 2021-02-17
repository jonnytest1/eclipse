package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

public class Testing {

	@Test
	public void howItsSupposedToLook() {
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		frame.setVisible(true);
		frame.setSize(1000, 900);

		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(Color.GREEN);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

		// this is how it should look
		JLabel fileLabel3 = new JLabel(
				"<html><div style=\"padding-left:1px;padding-top:10px\"><div style=\"border:1px;font-size: 10px;line-height: 11px; height:23px;overflow:hidden;width:"
						+ (100) + "px;white-space:nowrap;\">" + "fsdfesfevvveeeg" + "</div></div></html>");
		leftPanel.add(fileLabel3);

		frame.add(leftPanel, BorderLayout.LINE_START);

		JPanel centerPAnel = new JPanel();
		centerPAnel.setBackground(Color.RED);

		frame.add(centerPAnel, BorderLayout.CENTER);
		frame.validate();
		frame.repaint();

		boolean[] open = { true };
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent winEvt) {

				open[0] = false;
			}
		});
		while (frame.isActive()) {
			// wait for closing
		}

	}

	@Test
	public void failedTest() {

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		frame.setVisible(true);
		frame.setSize(1000, 900);

		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(Color.GREEN);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

		// this is how it actually looks

		JLabel fileLabel = new JLabel(
				"<html><div style=\"padding-left:1px;padding-top:10px\"><div style=\"border:1px;font-size: 10px;line-height: 11px; height:23px;overflow:hidden;width:"
						+ (100) + "px;white-space:nowrap;\">"
						+ "fsdfesfevvveeegggggggggggggggggggggggevevsdffffffffffffffffffffffffffffffffffffffffffffffffffffffffh"
						+ "</div></div></html>");
		fileLabel.setPreferredSize(new Dimension(100, 40));

		leftPanel.add(fileLabel);

		frame.add(leftPanel, BorderLayout.LINE_START);

		JPanel centerPAnel = new JPanel();
		centerPAnel.setBackground(Color.RED);

		frame.add(centerPAnel, BorderLayout.CENTER);
		frame.validate();
		frame.repaint();

		boolean[] open = { true };
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent winEvt) {

				open[0] = false;
			}
		});
		while (frame.isActive()) {
			// wait for closing
		}

	}

}
