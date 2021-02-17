package views.current.widgets.userstories;

import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import cfg_tp.EGitRepository;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class SingleUserStoryView extends CustomElementPanel implements ImplementedMouseListener {

	private static final long serialVersionUID = -2719344585380565932L;
	private transient UserStory userStory;

	public SingleUserStoryView(UserStory userStory) {

		this.userStory = userStory;
		// setOpaque(false);
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder(userStory.name);
		createTitledBorder.setBorder(BorderFactory.createLineBorder(null, 1));
		setBorder(createTitledBorder);
		BoxLayout mgr = new BoxLayout(this, BoxLayout.Y_AXIS);
		addMouseListener(this);
		setLayout(mgr);
		recreate();

	}

	@Override
	protected void addElements() {
		addTextArea(userStory.id + ":");
		addTextArea("git: " + userStory.matchingBranch);
		addTextArea("sap: " + userStory.sapProject);
		addTextArea("state: " + userStory.currentState);
		add(new TimeDisplayView(userStory));
	}

	private void addTextArea(String text) {
		JTextArea comp = new JTextArea(text);
		comp.setOpaque(false);
		add(comp);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		EGitRepository.setBugId(userStory.id + "");
	}

}
