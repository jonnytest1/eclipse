package views.current.widgets.userstories;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.JButton;
import javax.swing.JLabel;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.autocomplete.AutoCompleteTextField;
import socialdear.views.component.autocomplete.ComparableText;
import socialdear.views.component.implemented.customizable.ConfigurationPanel;

/**
 * @since 2.1
 */
public class UserStoryOptions extends ConfigurationPanel {

	public enum LABELS {
		FILTER, SORTING
	}

	private static final long serialVersionUID = -7044891783258227800L;

	public UserStoryOptions(JsonObject settings) {
		super(settings);
		setLayout(new GridLayout(2, 2));

	}

	@Override
	protected void addElements() {

		for (LABELS label : LABELS.values()) {
			add(new JLabel(label.name()));

			List<ComparableText> suggestions = new ArrayList<>();

			AutoCompleteTextField comp = new AutoCompleteTextField(suggestions, text -> {
				settingsBuilder.add(label.name(), Json.createValue(text));
			});

			if (previousSettings.containsKey(label.name())) {
				comp.setText(previousSettings.getString(label.name()));
			}

			comp.setEditable(true);
			add(comp);

		}

		JButton comp = new JButton("export csv");
		comp.addMouseListener(new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		add(comp);

	}
}
