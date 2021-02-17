package views.current.widgets.userstories;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class TimeDisplayView extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2636158772847484684L;

	private transient UserStory userStory;

	private transient JSlider slider;

	private transient JTextArea timeDisplay;

	TimeDisplayView(UserStory userStory) {

		this.userStory = userStory;
		recreate();
		BoxLayout mgr = new BoxLayout(this, BoxLayout.LINE_AXIS);
		setLayout(mgr);
		TimeManagement.register(userStory.id, this);
	}

	@Override
	protected void addElements() {
		slider = new JSlider(0, 100, getUserStory().currentTimeValue);
		slider.addChangeListener(e -> TimeManagement.update(getUserStory().id, slider.getValue()));
		add(slider, Component.LEFT_ALIGNMENT);

		timeDisplay = new JTextArea("0:00");
		timeDisplay.setMaximumSize(new Dimension(50, 30));
		add(timeDisplay);

	}

	public UserStory getUserStory() {
		return userStory;
	}

	public void setTime(int time) {
		int hours = time / 60;
		int min = (time - (hours * 60));
		String minutes = min + "";
		if (min < 10) {
			minutes += "0";
		}
		timeDisplay.setText(hours + ":" + minutes);
	}

}
