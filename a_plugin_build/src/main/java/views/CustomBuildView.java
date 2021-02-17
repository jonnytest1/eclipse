package views;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import builder.Builder;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.implemented.ProjectSelectionReceiver;

public class CustomBuildView extends ProjectSelectionReceiver {

	private IProject project;
	private List<String> messages = new ArrayList<>();

	public CustomBuildView() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
		recreate();

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -254467002842087352L;

	@Override
	protected void addElements() {

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(new CustomElementPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void init() {
				setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			}

			@Override
			protected void addElements() {

				JCheckBox incremnet = new JCheckBox("increment version", true);

				JButton build = new JButton("build");
				build.addMouseListener(new ImplementedMouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						messages.clear();
						Builder builder = new Builder(project, CustomBuildView.this);
						try {
							builder.build(incremnet.isSelected());
						} catch (CoreException | IOException | JSchException | SftpException e1) {
							SystemProperties.print(e1);
						}

					}
				});

				JButton upload = new JButton("upload");
				upload.addMouseListener(new ImplementedMouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						messages.clear();
						Builder builder = new Builder(project, CustomBuildView.this);
						try {
							builder.upload();
						} catch (IOException | JSchException | SftpException e1) {
							SystemProperties.print(e1);
						}
					}
				});

				add(build);
				add(incremnet);
				add(upload);
			}
		});

		add(getScrollPane(new CustomElementPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void init() {
				setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			}

			@Override
			protected void addElements() {

				for (String log : messages) {
					add(new JLabel(log));
				}
			}
		}));

	}

	public void addMessage(String message) {
		messages.add(message);
		recreate();
	}

}
