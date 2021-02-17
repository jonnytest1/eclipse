package socialdear.views.component.implemented.customizable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomIcon;

public class EditContainer extends JLayeredPane implements ImplementedMouseListener {

	enum CallbackOption {
		DELETE, SAVE, MOVE
	}

	public interface Callback {
		void callback(CallbackOption option, Object any);
	}

	private static final long serialVersionUID = 6230489670956793164L;
	private CustomizableViewPanel panel;
	private transient Callback callback;
	private int elements = 0;
	private JPanel editOptionsPanel;

	public EditContainer(CustomizableViewPanel panel, Callback onSave) {
		super();
		this.panel = panel;
		this.callback = onSave;

		editOptionsPanel = new JPanel();
		editOptionsPanel.setOpaque(false);

		addRemoveIcon();
		addSettingsIcon(panel);
		addDragIcon();
		editOptionsPanel.setBounds(0, -5, (elements * 20), 80);
		add(editOptionsPanel);

		setLayout(new BorderLayout());

		setLayer(editOptionsPanel, 2, 0);
		add(panel, 1);

		revalidate();
		repaint();
	}

	private void addDragIcon() {
		JLabel dragLabel = new JLabel(new CustomIcon("drag.png", 32, this));
		dragLabel.setOpaque(true);
		dragLabel.setBackground(Color.red);

		dragLabel.addMouseListener(new ImplementedMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				callback.callback(CallbackOption.DELETE, null);
			}
		});
		dragLabel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				//

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				callback.callback(CallbackOption.MOVE, e);

			}
		});
		editOptionsPanel.add(dragLabel);
		elements++;
	}

	private void addSettingsIcon(CustomizableViewPanel panel) {
		if (panel.getConfigurationOptions() != null) {

			JLabel settings = new JLabel(new CustomIcon("settings.png", this));
			settings.setOpaque(true);
			settings.setBackground(Color.red);
			settings.addMouseListener(this);

			editOptionsPanel.add(settings);
			elements++;
		}
	}

	void addRemoveIcon() {
		JLabel remove = new JLabel(new CustomIcon("delete.png", this));
		remove.setOpaque(true);
		remove.setBackground(Color.red);

		remove.addMouseListener(new ImplementedMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				callback.callback(CallbackOption.DELETE, null);
			}
		});
		elements++;
		editOptionsPanel.add(remove);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String key = "OptionPane.minimumSize";
		Object sizeValue = UIManager.get(key);

		UIManager.put(key, new Dimension(400, 200));
		UIManager.put("OptionPane.maximumSize", new Dimension(1500, 800));
		ConfigurationPanel configurationOptions = panel.getConfigurationOptions();
		JOptionPane.showMessageDialog(null, configurationOptions, "Customized Message Dialog",
				JOptionPane.PLAIN_MESSAGE);
		panel.setPanelOptions(configurationOptions.getSettings());
		callback.callback(CallbackOption.SAVE, null);
		UIManager.put(key, sizeValue);
	}
}
