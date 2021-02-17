package views.migration.components.sidebar;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

public class SideBarFileNameComponent extends CustomElementPanel implements ImplementedMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8327274241790243531L;

	private String fileName;

	private SQLCommandsComponent sqlCommandsComponent;

	SideBarFileNameComponent(String fileName, SQLCommandsComponent sqlCommandsComponent) {

		BoxLayout mgr = new BoxLayout(this, BoxLayout.LINE_AXIS);
		setLayout(mgr);

		setOpaque(true);

		// setOpaque(false);
		setAlignmentX(LEFT_ALIGNMENT);
		this.fileName = fileName;
		this.sqlCommandsComponent = sqlCommandsComponent;
		recreate();
	}

	@Override
	protected void addElements() {

		JCheckBox isCommandEnabled = new JCheckBox("");
		isCommandEnabled.setSelected(true);
		isCommandEnabled.setOpaque(false);
		isCommandEnabled.addMouseListener(this);
		add(isCommandEnabled);

		JLabel fileLabel = new JLabel(
				"<html><div style=\"padding-left:1px;padding-top:10px;width:190px\"><div style=\"border:1px;font-size: 10px;line-height: 11px; height:23px;overflow:hidden;width:"
						+ (100) + "px;white-space:nowrap;\">" + fileName + "</div></div></html>");
		fileLabel.setOpaque(true);
		fileLabel.setPreferredSize(new Dimension(190, 23));
		add(fileLabel);
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JCheckBox box = (JCheckBox) e.getSource();
		sqlCommandsComponent.setCommands(fileName, box.isSelected());

	}

}
