package views.current.widgets.userstories;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.1
 */
public class TimeManagement {

	private static Map<Integer, TimeDisplayView> userStoreis = new HashMap<>();

	private static int maxTime = 8 * 60;

	public static void register(Integer id, TimeDisplayView story) {
		userStoreis.put(id, story);
	}

	public static void update(Integer id, int newPercent) {

		userStoreis.get(id).getUserStory().currentTimeValue = newPercent;
		int allPercent = userStoreis.values().stream().mapToInt(view -> view.getUserStory().currentTimeValue).sum();
		userStoreis.values().stream().forEach(view -> {
			int userStoryPrecent = view.getUserStory().currentTimeValue;
			double viewPercent = 0;
			if (userStoryPrecent != 0) {
				viewPercent = (double) userStoryPrecent / allPercent;
			}
			int time = (int) (maxTime * viewPercent);
			view.setTime(time);
		});

	}
}
