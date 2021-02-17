package views.sap;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;

import http.SapServerRepository;
import model.WorkDurationEntry;
import model.WorkEntry;
import service.SAPListener;
import socialdear.logging.SystemProperties;
import socialdear.util.ExecutionException;
import socialdear.views.component.filter.implemented.FilterScrollComponent;
import socialdear.views.component.implemented.ProjectSelectionReceiver;

/**
 * @since 2.1
 */
public class SAPEntriesComponent extends ProjectSelectionReceiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3749494737769171500L;

	private transient IProject project;

	private transient Map<String, WorkDurationEntry> times = new ConcurrentHashMap<>();

	private SAPControlComponent sapControlComponent;

	private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

	private transient ScheduledFuture<?> scheduleAtFixedRate;

	private transient Calendar dataDay = Calendar.getInstance(TimeZone.getDefault());

	public SAPEntriesComponent() {

		setLayout(new BorderLayout());

		scheduleAtFixedRate = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
			Calendar currentDate = Calendar.getInstance(TimeZone.getDefault());
			if (currentDate.get(Calendar.DAY_OF_MONTH) != dataDay.get(Calendar.DAY_OF_MONTH)) {
				saveTimes();
				dataDay = currentDate;
			}

			try {
				calculateTimes();
				updateTimeSum();
			} catch (Exception e) {
				SystemProperties.print(e);
			}
		}, 0, 50, TimeUnit.SECONDS);

	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
		recreate();
	}

	public void saveTimes() {
		new SapServerRepository().sendTimes(times, dataDay);
		times.clear();
		SAPListener.clearEntries();
		calculateTimes();
		updateTimeSum();

	}

	@Override
	protected void cleanup() {
		scheduleAtFixedRate.cancel(false);
	}

	@Override
	protected void addElements() {
		calculateTimes();

		add(new FilterScrollComponent<WorkDurationEntry, SAPEntryComponent>(times.values(), SAPEntryComponent.class),
				BorderLayout.CENTER);

		sapControlComponent = new SAPControlComponent(this, project);
		add(sapControlComponent, BorderLayout.SOUTH);

		updateTimeSum();

	}

	private void calculateTimes() {
		WorkEntry previous = null;

		List<WorkEntry> filteredEntries = SAPListener.getEntries().stream() //
				.filter(entry -> entry.getProject() == project)//
				.collect(Collectors.toList());

		try {
			filteredEntries.add(SAPListener.getWorkEntry(project, null));
		} catch (ExecutionException e) {
			SystemProperties.print(e);
		}
		for (int i = 0; i < filteredEntries.size(); i++) {
			WorkEntry entry = filteredEntries.get(i);
			if (previous == null) {
				previous = entry;
			} else {
				if (!previous.getSapId().equals(entry.getSapId()) || i == filteredEntries.size() - 1) {
					addTime(previous, entry);
					previous = entry;
				}

			}
		}
	}

	private void addTime(WorkEntry previous, WorkEntry newEntry) {
		WorkDurationEntry duration = times.computeIfAbsent(previous.getSapId(), key -> new WorkDurationEntry(previous));
		duration.setNew(newEntry);

	}

	public void updateTimeSum() {
		if (sapControlComponent != null) {
			Long sum = times.values().stream()//
					.mapToLong(WorkDurationEntry::getDuration)//
					.sum();

			sapControlComponent.setTimeText(WorkDurationEntry.getDurationString(sum));
		}

	}

	public void addCustom(WorkDurationEntry entry) {
		times.put(entry.getSapId(), entry);
		recreate();

	}

}
