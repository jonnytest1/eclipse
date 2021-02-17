package views.current.widgets.userstories;

import java.awt.Component;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonObject;
import javax.swing.BoxLayout;

import http.TargetprocessRepository;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.customizable.CustomizableViewContainer;

/**
 * @since 2.1
 */
public class UserStoryListView extends CustomElementPanel {

	private static final long serialVersionUID = 1594025387740997055L;

	transient List<UserStory> userStories;

	public UserStoryListView(JsonObject settings) {
		userStories = new TargetprocessRepository() //
				.getUserStoriesDetailed(settings) //
				.stream() //
				.map(UserStory::new) //
				.collect(Collectors.toList());

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(CustomizableViewContainer.BACKGROUND_COLOR);

	}

	@Override
	protected void addElements() {
		for (UserStory userStory : userStories) {
			SingleUserStoryView comp = new SingleUserStoryView(userStory);
			comp.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(comp);
		}

	}

	void updateTimeSpent(int time, Integer id) {

	}

}
