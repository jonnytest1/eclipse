package a_plugin_struts.views.strutsviews;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import a_plugin_struts.model.ActionBeanObject;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public class StrutsJavaView extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1904981710905921160L;
	private ActionBeanObject actionobj;

	public StrutsJavaView(ActionBeanObject actionobj) {
		this.actionobj = actionobj;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		recreate();
	}

	@Override
	protected void addElements() {
		JLabel actionClass = new JLabel("action-class " + actionobj.getQualifiedActionClass());
		actionClass.addMouseListener(new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				SystemProperties.openMarker(actionobj.getClassMarker());
			}

		});
		add(actionClass);

		add(new JLabel("  "));

		if (actionobj.getFormBeanObject() != null) {
			add(new JLabel("  "));
			JLabel formClass = new JLabel("form-class " + actionobj.getFormBeanObject().getQualifiedFormClass());
			formClass.addMouseListener(new ImplementedMouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					SystemProperties.openMarker(actionobj.getFormBeanObject().getClassMarker());
				}

			});
			add(formClass);
		}

	}

}
