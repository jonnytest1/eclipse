package a_plugin_struts.views.strutsviews;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import a_plugin_struts.model.ActionBeanObject;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public class StrutsActionXmlView extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3805531042983047015L;
	private boolean isEnabled;
	private ActionBeanObject actionobj;

	StrutsActionXmlView(boolean isEnabled, ActionBeanObject actionobj) {
		this.isEnabled = isEnabled;
		this.actionobj = actionobj;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		recreate();
		setAlignmentX(Component.LEFT_ALIGNMENT);

	}

	@Override
	protected void addElements() {
		JLabel jLabel = new JLabel("action-xml: " + actionobj.getPath() + " - " + actionobj.getShortActionName());
		add(jLabel);
		if (isEnabled) {
			jLabel.addMouseListener(new ImplementedMouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					SystemProperties.openMarker(actionobj.getObjectMarker());
				}
			});

			if (actionobj.getFormBeanObject() != null) {

				add(new JLabel("  "));

				JLabel jLabel2 = new JLabel("form-xml: " + actionobj.getFormName());
				jLabel2.addMouseListener(new ImplementedMouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						SystemProperties.openMarker(actionobj.getFormBeanObject().getObjectMarker());
					}

				});
				this.add(jLabel2);

			}
		}

	}

}
