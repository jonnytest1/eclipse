package views.sap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Commit;
import model.WorkDurationEntry;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.filter.implemented.FilterChildComponent;

/**
 * @since 2.1
 */
public class SAPEntryComponent extends CustomElementPanel
		implements FilterChildComponent<WorkDurationEntry>, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3851503262460332980L;

	private transient WorkDurationEntry workEntry;

	private static final int EIGHT_HOURS = 8 * 60 * 60 * 1000;

	private JLabel durationLAbel;

	public SAPEntryComponent() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	protected void addElements() {
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder(workEntry.getSapId());
		createTitledBorder.setBorder(BorderFactory.createLineBorder(null, 1));
		setBorder(createTitledBorder);
		add(new CustomElementPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void addElements() {
				setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

				durationLAbel = new JLabel(workEntry.getDurationReadable());
				add(durationLAbel);
				JSlider jSlider = new JSlider(0, EIGHT_HOURS, workEntry.getDurationInt());
				jSlider.addChangeListener(SAPEntryComponent.this);
				add(jSlider);
			}
		});

		for (Commit commit : workEntry.getCommits()) {
			add(new JLabel(commit.getMessage()));
		}

	}

	@Override
	public void setElement(WorkDurationEntry element) {
		this.workEntry = element;
		element.setEntriesComponent(this);
		recreate();

	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		JSlider source = (JSlider) arg0.getSource();

		int fps = source.getValue();

		workEntry.setDuration(fps);
		refreshDuration();
	}

	public void refreshDuration() {
		durationLAbel.setText(workEntry.getDurationReadable());
	}

}
