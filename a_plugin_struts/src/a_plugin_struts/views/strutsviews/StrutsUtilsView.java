package a_plugin_struts.views.strutsviews;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Display;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.StrutsProperties.STRUTS_SETTINGS;
import a_plugin_struts.model.ActionBeanObject;
import a_plugin_struts.model.FilesContainer;
import a_plugin_struts.service.StrutsConverter;
import a_plugin_struts.views.StrutsProjectPage;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.Visualization;

public class StrutsUtilsView extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3413741290956114611L;
	private ActionBeanObject actionobj;
	private FilesContainer files;

	public StrutsUtilsView(ActionBeanObject actionobj, FilesContainer files2) {
		this.actionobj = actionobj;
		this.files = files2;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		recreate();
	}

	@Override
	protected void addElements() {
		if (actionobj.getFormBeanObject() != null) {
			addButton("convert", new ImplementedMouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					new StrutsConverter(actionobj, files).convert();
				}
			});
		}

		add(new JLabel("  "));
		addButton("generate from here", new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!StrutsProjectPage.finishedParsingFiles) {
					Display.getDefault().asyncExec(() -> {
						TitleAreaDialog titleAreaDialog = new TitleAreaDialog(null);
						titleAreaDialog.setMessage("didnt finish parsing everything");
						titleAreaDialog.open();

					});
				}

				File img;
				try {
					img = new Visualization().visualize(actionobj, StrutsProperties.getIntegerValue(STRUTS_SETTINGS.GRAPH_VIZ_DEPTHS));
					Desktop.getDesktop().open(img);
				} catch (IOException e1) {
					SystemProperties.print(e1);
				}

			}
		});

	}

}
