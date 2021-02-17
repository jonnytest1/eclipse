package socialdear.views.component.implemented.customizable;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public class CustomizableViewContainer extends CustomElementPanel {

	public static final Color BACKGROUND_COLOR = Color.white;

	private static final long serialVersionUID = 5226818428181021757L;

	private Map<Integer, Map<Integer, CustomizableViewPanel>> components = new HashMap<>();

	private boolean editMode = false;

	private transient OnSave save;

	private List<CustomizableViewPanel> config;

	private int highestGridY = -1;

	private int highestGridX = -1;

	private Long lastSwitch = 0L;

	private List<Class<? extends CustomizableViewPanel>> library;

	public CustomizableViewContainer(List<CustomizableViewPanel> els, OnSave save,
			List<Class<? extends CustomizableViewPanel>> library) {

		this.config = els;
		this.save = save;
		this.library = library;
		setLayout(new GridBagLayout());
		setBackground(BACKGROUND_COLOR);
	}

	@Override
	protected void addElements() {
		removeAll();
		for (CustomizableViewPanel entry : config) {

			CustomizableViewPanel panel = entry;
			panel.setBackground(BACKGROUND_COLOR);
			if (editMode) {
				panel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			} else {
				panel.setBorder(null);
			}
			GridBagConstraints constraints = entry.getConstraints();
			constraints.weightx = 1;
			constraints.weighty = 1;
			setConfigButton(constraints, panel);

		}
		if (editMode) {
			addEditButtons();
		}

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = highestGridY + (editMode ? 4 : 2);
		c.gridx = 0;
		c.gridwidth = highestGridX + (editMode ? 2 : 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel edit = new JLabel("edit mode", SwingConstants.CENTER);
		edit.setBackground(Color.lightGray);
		edit.setOpaque(true);
		edit.addMouseListener(new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				editMode = !editMode;
				if (!editMode) {
					removeDefaults();
					save.save(config);
				}
				recreate();
			}
		});
		add(edit, c);
	}

	private void removeDefaults() {
		config.removeIf(cfg -> {
			if (cfg instanceof DefaultWidget) {
				GridBagConstraints constraints = cfg.getConstraints();
				components.get(constraints.gridx).remove(constraints.gridy);
				return true;
			}
			return false;
		});
	}

	@Override
	public void recreate() {
		highestGridX = -1;
		highestGridY = -1;
		super.recreate();
	}

	void addDefaultComponent(int i, int j) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = i;
		c.gridy = j;
		c.weightx = 1;
		c.weighty = 1;

		updateConstraints(i, j, c);

		if (j == highestGridY + 1 && i == highestGridX + 1 && highestGridX > -1 && highestGridY > -1) {
			return;
		}
		CustomizableViewPanel panel = getDefaultPanel();

		if (j == highestGridY + 1 || i == highestGridX + 1) {
			//
		} else {
			if (components.get(c.gridx) == null) {
				components.put(c.gridx, new HashMap<>());
			}
			components.get(c.gridx).put(c.gridy, panel);
			config.add(panel);
		}
		setEditButton(c, panel);

	}

	private CustomizableViewPanel getDefaultPanel() {
		CustomizableViewPanel panel = new DefaultWidget();
		panel.addMouseListener(new ImplementedMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String[] choices = library.stream().map(Class::getSimpleName).toArray(String[]::new);
				String input = (String) JOptionPane.showInputDialog(null, "Choose now...", "The Choice of a Lifetime",
						JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				Optional<Class<? extends CustomizableViewPanel>> newPanelClass = library.stream()
						.filter(cl -> cl.getSimpleName().equals(input)).findFirst();
				if (newPanelClass.isPresent()) {
					try {
						CustomizableViewPanel newPanelInstance = newPanelClass.get().getConstructor().newInstance();

						GridBagConstraints newC = new GridBagConstraints();
						newC.gridy = panel.getConstraints().gridy;
						newC.gridx = panel.getConstraints().gridx;
						newPanelInstance.setConstraints(newC);
						removeDefaults();
						config.add(newPanelInstance);
						recreate();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
						SystemProperties.print(e1);
					}
				}

				save.save(config);
			}
		});
		return panel;
	}

	private void updateConstraints(int i, int j, GridBagConstraints c) {
		if (i == highestGridX + 1) {
			c.weightx = 0;
		}
		if (j == highestGridY + 1) {
			c.weighty = 0;
		}
	}

	private void addEditButtons() {
		int amountX = highestGridX;
		int amountY = highestGridY;
		for (int i = 0; i <= amountX + 1; i++) {
			for (int j = 0; j <= amountY + 1; j++) {
				addEditButton(i, j);
			}
		}

	}

	private void addEditButton(int i, int j) {
		if (components.get(i) == null || components.get(i).get(j) == null) {
			addDefaultComponent(i, j);
		} else {
			CustomizableViewPanel component = components.get(i).get(j);
			if (component instanceof DefaultWidget) {
				updateConstraints(i, j, component.getConstraints());
				remove(component);
				add(component, component.getConstraints());
				revalidate();
			} else {
				remove(component);
				EditContainer comp = new EditContainer(component, (option, param) -> {
					switch (option) {
					case DELETE:
						removeComponent(component);
						break;
					case SAVE:
						save.save(config);
						component.recreate();
						break;
					case MOVE:
						checkPositions(component, (MouseEvent) param);
						break;
					default:
						break;
					}

				});
				add(comp, component.getConstraints());
			}
		}
	}

	void removeComponent(CustomizableViewPanel component) {
		config.removeIf(el -> el == component);
		save.save(config);
		components.clear();
		// ((CustomElementPanel) getParent()).
		recreate();
	}

	private void checkPositions(CustomizableViewPanel panel, MouseEvent e) {
		CustomizableViewPanel right = panel.getWithOffset(1, 0, components);
		if (right != null && e.getX() > panel.getWidth() + right.getWidth() / 2) {
			swap(panel, right);

			recreate();
		}

		CustomizableViewPanel left = panel.getWithOffset(-1, 0, components);
		if (left != null && e.getX() < 0 - left.getWidth() / 2) {
			swap(panel, left);

			recreate();
		}

		CustomizableViewPanel top = panel.getWithOffset(0, -1, components);
		if (top != null && e.getY() < 0 - top.getHeight() / 2) {
			swap(panel, top);
			recreate();
		}

		CustomizableViewPanel bottom = panel.getWithOffset(0, 1, components);
		if (bottom != null && e.getY() > panel.getHeight() + bottom.getHeight() / 2) {
			swap(panel, bottom);
			recreate();
		}

	}

	private void swap(CustomizableViewPanel panel1, CustomizableViewPanel panel2) {
		Long now = System.currentTimeMillis();
		if (lastSwitch < now - 200) {
			lastSwitch = now;
			GridBagConstraints c = panel1.getConstraints();
			panel1.setConstraints(panel2.getConstraints());
			panel2.setConstraints(c);
		}
	}

	private void setEditButton(GridBagConstraints constraints, CustomizableViewPanel panel) {
		constraints.fill = GridBagConstraints.BOTH;
		if (components.get(constraints.gridx) == null) {
			components.put(constraints.gridx, new HashMap<>());
		}
		if (editMode) {
			panel.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
					//
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					checkPositions(panel, e);

				}
			});
		}
		panel.setConstraints(constraints);
		add(panel, constraints);
	}

	private void setConfigButton(GridBagConstraints constraints, CustomizableViewPanel panel) {

		if (!(panel instanceof DefaultWidget)) {
			if (constraints.gridy > highestGridY) {
				highestGridY = constraints.gridy;
			}
			if (constraints.gridx > highestGridX) {
				highestGridX = constraints.gridx;
			}
		}

		setEditButton(constraints, panel);
		components.get(constraints.gridx).put(constraints.gridy, panel);
	}

}
