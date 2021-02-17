package views.current.widgets.bugview;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import cfg_tp.EGitRepository;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class SingleBugView extends CustomElementPanel implements ImplementedMouseListener {

	private static final long serialVersionUID = 3911325224649511921L;

	private Integer id;

	private String name;

	public SingleBugView(JsonValue bug) {
		JsonObject bugO = bug.asJsonObject();
		id = bugO.getInt("id");
		name = bugO.getString("name");
		setOpaque(false);
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder(name);
		createTitledBorder.setBorder(BorderFactory.createLineBorder(null, 1));
		setBorder(createTitledBorder);
		BoxLayout mgr = new BoxLayout(this, BoxLayout.LINE_AXIS);
		addMouseListener(this);
		setLayout(mgr);
		recreate();
	}

	@Override
	protected void addElements() {
		add(new JLabel(id + ":"));
		add(Box.createRigidArea(new Dimension(5, 0)));

		String bugName = name;
		if (bugName.length() > 65) {
			bugName = bugName.substring(0, 65) + "...";
		}
		// add(new JLabel(bugName));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		EGitRepository.setBugId(id + "");
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(2000, 20000);
	}
}
