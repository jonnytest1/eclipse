package views.migration.components.sidebar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;
import sql.SQLCommand;

public class SideBarSQLCommandComponent extends CustomElementPanel implements ImplementedMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3525358622679079109L;

	private SQLCommand command;

	private int width;

	private boolean isEnabled = true;

	private boolean parsable = true;

	private JCheckBox isCommandEnabled;

	private SQLCommandsComponent sqlCommandsComponent;

	public SideBarSQLCommandComponent(SQLCommand command, int width, SQLCommandsComponent sqlCommandsComponent) {
		this.sqlCommandsComponent = sqlCommandsComponent;
		command.setSelectionElement(this);
		this.command = command;
		this.width = width;
		BoxLayout mgr = new BoxLayout(this, BoxLayout.LINE_AXIS);
		setLayout(mgr);
		setAlignmentX(LEFT_ALIGNMENT);
		recreate();
		setPreferredSize(new Dimension(width, 20));
	}

	@Override
	protected void addElements() {

		isCommandEnabled = new JCheckBox("");
		isCommandEnabled.setSelected(isEnabled);
		isCommandEnabled.addMouseListener(this);
		if (!parsable) {
			setBackground(Color.RED);
		}

		add(isCommandEnabled);

		JLabel comp = new JLabel(
				"<html><div style=\"padding-left:10px;padding-top:13px\"><div style=\"border:1px;border-radius:200px;font-size: 8px;line-height: 11px; height:23px;overflow:hidden;width:"
						+ width + "px;white-space: nowrap;\">"
						+ command.getShortForm().split("\n")[0].replaceAll(" \\(", "") + "</div></div></html>");

		comp.setMaximumSize(new Dimension(width, 30));
		add(comp);

	}

	public void setFailed() {
		setBackground(Color.red);
	}

	public void setSuccessful() {
		setBackground(null);
	}

	public void setParsable(boolean parsable) {
		if (parsable != this.parsable) {
			this.parsable = parsable;
			recreate();
		}
	}

	public void setSelected(boolean selected) {
		isCommandEnabled.setSelected(selected);
		isEnabled = selected;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		command.setEnabled(!command.isEnabled());
		sqlCommandsComponent.rebuildDataBaseView();
	}

}
