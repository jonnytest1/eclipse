package views.current.widgets.bugview;

import static socialdear.views.ImplementedPreferences.getValue;

import java.awt.Component;
import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.swing.BoxLayout;

import cfg_tp.SystemSettings;
import cfg_tp.SystemSettings.TP_SETTINGS;
import socialdear.http.CustomHttp;
import socialdear.http.CustomResponse;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.customizable.CustomizableViewContainer;
import views.current.widgets.bugview.BugViewOptions.LABELS;

/**
 * @since 2.1
 */
public class BugListView extends CustomElementPanel {

	private static final long serialVersionUID = 132857499442980305L;

	transient JsonArray bugs;

	private transient JsonObject settings;

	public BugListView(JsonObject settings) {
		this.settings = settings;
		bugs = getBugs();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(CustomizableViewContainer.BACKGROUND_COLOR);

	}

	@Override
	protected void addElements() {
		for (JsonValue bug : bugs) {
			SingleBugView comp = new SingleBugView(bug);
			comp.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(comp);
		}

	}

	JsonArray getBugs() {
		String url = "https://" + getValue(TP_SETTINGS.TARGET_PROCESS_DOMAIN) + "/api/v2/Bugs?select={id,name}";

		try {
			CustomHttp request = new CustomHttp().target(url).addQuery("take", "100");

			if (settings.containsKey(LABELS.FILTER.name())) {
				request.addQuery("filter", "?" + settings.getString(LABELS.FILTER.name()));
				// .addQuery("filter", "?(TeamIteration is Current or TeamIteration is Future)")
			}
			CustomResponse projects = request.auth(SystemSettings.getAuth()).request().get();
			return projects.json().asJsonObject().getJsonArray("items");
		} catch (IOException e) {
			return null;
		}
	}

}
