package model;

import org.eclipse.core.resources.IProject;

/**
 * @since 2.1
 */
public class WorkEntry {

	String sapId;
	String gitBranch;
	Long timestamp;
	private IProject project;

	public WorkEntry(String sapId, String gitBranch, IProject project) {
		this.sapId = sapId;
		this.gitBranch = gitBranch;
		this.project = project;
		timestamp = System.currentTimeMillis();
	}

	public String getSapId() {
		return sapId;
	}

	public String getGitBranch() {
		return gitBranch;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public IProject getProject() {
		return project;
	}

}
