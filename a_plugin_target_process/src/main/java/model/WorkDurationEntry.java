package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;

import socialdear.logging.SystemProperties;
import socialdear.util.Executer;
import socialdear.util.ExecutionException;
import views.sap.SAPEntryComponent;

/**
 * @since 2.1
 */
public class WorkDurationEntry {

	private String sapId;
	private Long duration = 0L;
	private String gitBranch;
	private IProject project;

	List<Commit> commits = new ArrayList<>();

	private SAPEntryComponent sapEntryComponent;
	private WorkEntry newest;

	public WorkDurationEntry(WorkEntry previous) {
		this.sapId = previous.getSapId();
		gitBranch = previous.getGitBranch();
		project = previous.getProject();
		this.newest = previous;
		try {

			String author = new Executer().run("git config user.email", project.getLocationURI()).split("\n")[1];

			String commitLog = new Executer().run(
					"git log " + gitBranch + " --since=\"12 hours ago\" --author=\"" + author + "\" --date=relative",
					project.getLocationURI()).trim();
			String[] split = commitLog.split("commit ");
			for (int i = 1; i < split.length; i++) {
				commits.add(new Commit(split[i]));
			}
		} catch (ExecutionException e) {
			if (!e.getError().contains("ot a git repository")) {
				SystemProperties.print(e);
			}
		}

	}

	public WorkDurationEntry(String sapId, IProject project) {
		this.sapId = sapId;
		this.project = project;
		this.newest = new WorkEntry(sapId, null, project);
	}

	public String getSapId() {
		return sapId;
	}

	public String getDurationReadable() {

		return getDurationString(duration);
	}

	public static String getDurationString(Long duration) {
		long hours = TimeUnit.MILLISECONDS.toHours(duration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- (TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes));

		return String.format("%d Stunden, %d Minuten %d seconds", hours, minutes, seconds);

	}

	public IProject getProject() {
		return project;
	}

	public void setDuration(long l) {
		duration = l;
	}

	public Long getDuration() {
		return duration;
	}

	public int getDurationInt() {
		return duration.intValue();
	}

	public List<Commit> getCommits() {
		return commits;

	}

	public void setEntriesComponent(SAPEntryComponent sapEntryComponent) {
		this.sapEntryComponent = sapEntryComponent;
	}

	public void setNew(WorkEntry newEntry) {
		if (newest != newEntry) {
			this.duration += newEntry.getTimestamp() - newest.getTimestamp();
			if (sapEntryComponent != null) {
				sapEntryComponent.refreshDuration();
			}
		}
		this.newest = newEntry;

	}

}
