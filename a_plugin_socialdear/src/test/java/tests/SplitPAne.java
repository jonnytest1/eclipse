package tests;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.junit.jupiter.api.Test;

import lib.JFrameTest;

public class SplitPAne extends JFrameTest {
	@Test
	public void test() {

		// Erzeugung zweier JPanel-Objekte
		JPanel panelRot = new JPanel();
		JPanel panelGelb = new JPanel();
		// Hintergrundfarben der JPanels werden gesetzt
		panelRot.setBackground(Color.red);
		panelGelb.setBackground(Color.yellow);
		// Beschriftungen für die beiden Seiten werden erstellt
		JLabel labelRot = new JLabel("Ich bin auf der roten Seite");
		JLabel labelGelb = new JLabel("Ich bin auf der gelben Seite");
		// Labels werden unseren Panels hinzugefügt
		panelRot.add(labelRot);
		panelGelb.add(labelGelb);

		// Erzeugung eines JSplitPane-Objektes mit horizontaler Trennung
		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// Hier setzen wir links unser rotes JPanel und rechts das gelbe
		splitpane.setLeftComponent(panelRot);
		splitpane.setRightComponent(panelGelb);

		// Hier fügen wir unserem Dialog unser JSplitPane hinzu
		frame.add(splitpane);
		// Wir lassen unseren Dialog anzeigen
		frame.setVisible(true);
	}

}
