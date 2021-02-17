package views.launcher;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import model.launcher.Branch;
import model.launcher.Server;
import model.launcher.Task;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

public class LauncherBranchContainerComponent extends CustomElementPanel implements ImplementedMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2078522678290549740L;
	private transient Branch branch;

	transient boolean isEnabled = false;

	public static final int ICON_SIZE = 15;

	boolean needsReFetch;
	private boolean colored;
	private String filter;

	LauncherBranchContainerComponent(Branch branch, boolean colored, String filter) {
		this.branch = branch;
		this.colored = colored;
		this.filter = filter;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		addMouseListener(this);
		recreate();
	}

	@Override
	protected void addElements() {
		add(new LauncherBranchTitleComponent(branch, colored));
		needsReFetch = false;

		int elements = 0;
		for (Server server : branch.getServers()) {
			if (!server.matchesFilter(filter)) {
				continue;
			}
			for (Task task : server.getTasks()) {
				if (isEnabled || task.isJavaRemote()) {
					elements++;
					add(new LauncherTaskComponent(task, server));
				}
			}
		}
		if (elements > 0) {
			setBorder(BorderFactory.createLineBorder(Color.black));
		} else {
			setBorder(null);
		}
	}

	public boolean needsReFetch() {
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		isEnabled = !isEnabled;
		recreate();
	}

}
