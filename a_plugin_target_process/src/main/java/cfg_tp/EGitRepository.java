package cfg_tp;

import org.eclipse.core.resources.IResource;
import org.eclipse.egit.ui.ICommitMessageProvider;

/**
 * @since 2.1
 */
public class EGitRepository implements ICommitMessageProvider {

	private static String bugId = "";

	@Override
	public String getMessage(IResource[] arg0) {
		return bugId;
	}

	public static void setBugId(String id) {
		EGitRepository.bugId = "#" + id;
	}
}
