package views.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextPane;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import model.launcher.Server;
import model.launcher.Task;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.CustomIcon;

public class LauncherTaskComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9152659718448531564L;

	private transient Task task;

	private boolean needsRefetch = false;

	private transient Server server;

	public LauncherTaskComponent(Task task, Server server) {
		this.task = task;
		this.server = server;
		recreate();
	}

	@Override
	protected void addElements() {

		JTextPane textAreaServer = new JTextPane();
		textAreaServer.setText(server.getShortId().split("-from")[0]);
		textAreaServer.setOpaque(true);

		if ("TASK_RUNNING".equals(task.getHealthState())) {
			setBackground(Color.GREEN);
		} else if ("TASK_FAILED".equals(task.getHealthState())) {
			setBackground(Color.RED);
		} else if ("TASK_STAGING".equals(task.getHealthState()) || "TASK_STARTING".equals(task.getHealthState())) {
			setBackground(Color.BLUE);
			needsRefetch = true;
		} else {
			textAreaServer.setOpaque(false);
		}
		add(textAreaServer, BorderLayout.CENTER);
		if (task.getPort() != null) {
			JLabel changesNotification = new JLabel(new CustomIcon("attach-folded-icon.png", this));
			changesNotification.setOpaque(false);
			add(changesNotification, BorderLayout.LINE_END);

			changesNotification.addMouseListener(new ImplementedMouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					attachDebugger(task);

				}
			});
		}

	}

	private void attachDebugger(Task task) {
		try {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

			ILaunchConfigurationType launchType = manager
					.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy wc;
			wc = launchType.newInstance(null, manager.generateLaunchConfigurationName(task.getServer().getShortId()));
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, task.getServer().getProjectName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE, false);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
					IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);

			Map<String, String> argMap = new HashMap<>();
			argMap.put("hostname", task.getHost());
			argMap.put("port", task.getPort());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, argMap);

			ILaunchConfiguration lc = wc.doSave();
			// ILaunch launch =
			lc.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
		} catch (CoreException e1) {
			SystemProperties.print("failed launching debug", e1);
		}
	}

	public boolean isNeedsRefetch() {
		return needsRefetch;
	}
}
