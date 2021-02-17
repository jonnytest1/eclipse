package views.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import model.launcher.Branch;
import model.launcher.Server;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;
import util.KibanaLogService;

public class LauncherBranchTitleComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8059654513444364733L;
	private transient Branch branch;

	public LauncherBranchTitleComponent(Branch branch, boolean colored) {
		if (colored) {
			setBackground(Color.LIGHT_GRAY);
		}
		this.branch = branch;
		recreate();
	}

	@Override
	protected void addElements() {

		JLabel textArea = new JLabel();
		textArea.setText(branch.getName());
		textArea.setOpaque(false);

		add(textArea, BorderLayout.CENTER);
		add(addLogIcon(), BorderLayout.LINE_END);
		addScaleDropdown();
	}

	private void addScaleDropdown() {
		if (branch.getServers().stream().allMatch(server -> server.getInstances() != null)) {
			JComboBox<Integer> instances = new JComboBox<>();

			int maxInstances = 0;
			for (Server server : branch.getServers()) {
				maxInstances = Math.max(maxInstances, server.getInstances());
			}

			for (int i = 0; i < 6 || i <= maxInstances; i++) {
				instances.addItem(i);
			}
			instances.setSelectedIndex(maxInstances);

			instances.addActionListener(e -> {
				Integer amount = (Integer) ((JComboBox<?>) e.getSource()).getSelectedItem();
				branch.scale(amount);
			});
			add(instances, BorderLayout.LINE_END);
		}
	}

	private JLabel addLogIcon() {
		URL logImagePath = this.getClass().getResource("/icons/log.png");
		ImageIcon logImageICon = new ImageIcon(logImagePath);
		Image logImage = logImageICon.getImage().getScaledInstance(LauncherBranchContainerComponent.ICON_SIZE,
				LauncherBranchContainerComponent.ICON_SIZE, Image.SCALE_DEFAULT);
		ImageIcon scaledIIcon = new ImageIcon(logImage);
		JLabel logImageLabel = new JLabel(scaledIIcon);
		Boolean enabled = KibanaLogService.getLogging(branch);
		if (enabled != null && enabled) {
			logImageLabel.setBackground(Color.GREEN);
		}
		logImageLabel.setOpaque(true);
		add(logImageLabel);

		logImageLabel.addMouseListener(new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!KibanaLogService.switchLogging(branch)) {
					logImageLabel.setBackground(Color.GREEN);
				} else {
					logImageLabel.setBackground(Color.white);
				}
				KibanaLogService.getLogs();
			}

		});
		return logImageLabel;
	}

}
