package socialdear.util.files;

import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;

@SuppressWarnings("restriction")
public class ManualResourceDelta extends ResourceDelta {

	private final IProject project;

	public ManualResourceDelta(IProject project) {
		super(project.getFullPath(), null);
		this.project = project;
		status = IResourceDelta.CHANGED;
	}

	public IProject getProject() {
		return project;
	}

}
