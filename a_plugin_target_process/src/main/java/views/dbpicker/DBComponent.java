package views.dbpicker;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.eclipse.core.resources.IProject;

import cfg_tp.FileRepository;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.util.files.FileParser;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.filter.implemented.FilterChildComponent;

/**
 * @since 2.1
 */
public class DBComponent extends CustomElementPanel implements FilterChildComponent<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7827321862300200555L;
	private String dbName = "";

	public DBComponent() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	@Override
	protected void addElements() {
		JLabel comp = new JLabel(dbName);
		add(comp);
		add(Box.createRigidArea(new Dimension(20, 0)));
		JLabel set = new JLabel("to gralde properties local");
		set.addMouseListener(new ImplementedMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (IProject project : FileParser.getProjects()) {
					FileRepository.setGradlePropertiesAttribute(project, "docker.container", dbName);
				}

			}
		});
		add(set);
	}

	@Override
	public void setElement(String element) {
		this.dbName = element;
		recreate();

	}

}
