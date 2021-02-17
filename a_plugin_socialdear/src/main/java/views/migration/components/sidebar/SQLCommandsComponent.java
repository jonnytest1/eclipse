package views.migration.components.sidebar;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;

import socialdear.views.component.CustomElementPanel;
import sql.SQLCommand;

public class SQLCommandsComponent extends CustomElementPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 7167097464879970321L;

	private List<SQLCommand> sqlCommands;

	private Map<String, List<SideBarSQLCommandComponent>> childComponents = new HashMap<>();

	private SideBarComponent sideBarComponent;

	public SQLCommandsComponent(List<SQLCommand> list, SideBarComponent sideBarComponent) {
		this.sqlCommands = list;
		this.sideBarComponent = sideBarComponent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		addElements();
	}

	@Override
	protected void addElements() {
		int width = 190;
		String fileName = null;
		childComponents = new HashMap<>();
		int elements = 0;
		for (SQLCommand command : sqlCommands) {
			if (fileName == null || !fileName.equals(command.getFileName())) {
				SideBarFileNameComponent fileNameComponent = new SideBarFileNameComponent(command.getFileName(), this);
				add(fileNameComponent);
				elements++;
				fileName = command.getFileName();
			}
			SideBarSQLCommandComponent comp = new SideBarSQLCommandComponent(command, width, this);
			getCommandList(fileName).add(comp);
			add(comp);
			elements++;
		}

		setPreferredSize(new Dimension(180, elements * 30));
	}

	List<SideBarSQLCommandComponent> getCommandList(SideBarFileNameComponent file) {
		return getCommandList(file.getFileName());
	}

	List<SideBarSQLCommandComponent> getCommandList(String file) {
		List<SideBarSQLCommandComponent> commands = childComponents.get(file);
		if (commands == null) {
			commands = new ArrayList<>();
			childComponents.put(file, commands);

		}
		return commands;
	}

	public Map<String, List<SideBarSQLCommandComponent>> getChildComponents() {
		return childComponents;
	}

	public void setCommands(String fileName, Boolean enabled) {
		for (SideBarSQLCommandComponent command : getCommandList(fileName)) {
			command.setSelected(enabled);
		}
		rebuildDataBaseView();
	}

	public void rebuildDataBaseView() {
		sideBarComponent.rebuildDataBaseView();

	}
}
