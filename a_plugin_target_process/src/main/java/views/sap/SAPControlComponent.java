package views.sap;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.eclipse.core.resources.IProject;

import model.WorkDurationEntry;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class SAPControlComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5525241757347688695L;
	private JLabel jLabel;
	private SAPEntriesComponent sapEntriesComponent;
	private transient IProject project;

	SAPControlComponent(SAPEntriesComponent sapEntriesComponent, IProject project) {
		this.sapEntriesComponent = sapEntriesComponent;
		this.project = project;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		jLabel = new JLabel();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, 50);
	}

	@Override
	protected void addElements() {

		add(jLabel);
		JButton saveButton = new JButton("save");
		saveButton.addMouseListener(new ImplementedMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sapEntriesComponent.saveTimes();
			}
		});
		add(saveButton);
		JTextArea jTextArea = new JTextArea();
		add(jTextArea);

		JButton jButton = new JButton("add custom");
		jButton.addMouseListener(new ImplementedMouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String sap = jTextArea.getText();
				WorkDurationEntry entry = new WorkDurationEntry(sap, project);
				sapEntriesComponent.addCustom(entry);

			}
		});
		add(jButton);
	}

	public void setTimeText(String durationString) {
		jLabel.setText(durationString);

	}

}
