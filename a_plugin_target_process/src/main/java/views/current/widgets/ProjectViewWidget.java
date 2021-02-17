package views.current.widgets;

import javax.swing.JLabel;

import socialdear.views.component.implemented.customizable.CustomizableViewContainer;
import socialdear.views.component.implemented.customizable.CustomizableViewPanel;

/**
 * @since 2.1
 */
public class ProjectViewWidget extends CustomizableViewPanel {

	private static final long serialVersionUID = 7143126109420400953L;

	public ProjectViewWidget() {
		setBackground(CustomizableViewContainer.BACKGROUND_COLOR);
		recreate();
	}

	@Override
	protected void addElements() {
		add(new JLabel("projects"));
	}

}
