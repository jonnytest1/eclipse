package a_plugin_struts.views.strutsviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import a_plugin_struts.model.ActionBeanObject;
import a_plugin_struts.model.FilesContainer;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.implemented.filter.FilterableComponent;

public class StrutsActionView extends FilterableComponent implements ImplementedMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8161167802431584766L;

	private boolean isEnabled = false;

	private transient ActionBeanObject actionobj;

	private FilesContainer files;

	public StrutsActionView(List<ActionBeanObject> list, FilesContainer files) {
		this.actionobj = list.get(0);
		this.files = files;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(null);
		addMouseListener(this);
		recreate();

	}

	@Override
	protected void addElements() {
		if (isEnabled) {
			setBorder(BorderFactory.createCompoundBorder( //
					BorderFactory.createLineBorder(Color.black, 2, true), //
					new EmptyBorder(1, 4, 2, 4)));

		} else {
			setBorder(null);
		}
		add(new StrutsActionXmlView(isEnabled, actionobj));

		if (isEnabled) {

			add(new StrutsJavaView(actionobj));

			add(new StrutsUtilsView(actionobj, files));

		}

	}

	@Override
	protected String getMatcherString() {
		return actionobj.getQualifiedActionClass() + actionobj.getFormName() + actionobj.getPath();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		isEnabled = !isEnabled;
		String page = actionobj.getPage();
		if (isEnabled && actionobj.getPageFiles() == null && page != null) {
			actionobj.setPagesFiles();
			files.getXmlFiles().parallelStream()//
					.filter(file -> file.getAbsolutePath().endsWith(page.replaceAll("/", "\\\\")))//
					.forEach(file -> actionobj.addPageFile(file));
		}
		recreate();
	}

	public ActionBeanObject getActionobj() {
		return actionobj;
	}

}
