package socialdear.views.component.implemented;

import org.eclipse.core.resources.IProject;

import socialdear.views.component.CustomElementPanel;

public abstract class ProjectSelectionReceiver extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4992101708500442809L;

	public abstract void setProject(IProject project);

}
