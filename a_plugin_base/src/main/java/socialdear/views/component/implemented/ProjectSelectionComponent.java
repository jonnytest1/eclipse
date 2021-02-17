package socialdear.views.component.implemented;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import socialdear.exceptions.ConstructionException;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.CustomIcon;

public class ProjectSelectionComponent extends CustomElementPanel implements ActionListener, ImplementedMouseListener {

	private static final long serialVersionUID = 3841679484791418070L;

	transient IProject activeProject = null;

	transient List<IProject> projects = new ArrayList<>();

	private transient Predicate<? super IProject> filter;

	private ProjectSelectionReceiver projectDependentChild;

	String originClass;

	private transient IEclipsePreferences scopedPreferenceStore;

	private Class<? extends ProjectSelectionReceiver> childClass;

	public ProjectSelectionComponent(Class<? extends ProjectSelectionReceiver> child) {
		this(child, p -> !p.getName().equals("RemoteSystemsTempFiles"));
	}

	public ProjectSelectionComponent(Class<? extends ProjectSelectionReceiver> child,
			Predicate<? super IProject> filter) {
		this.childClass = child;
		try {
			projectDependentChild = child.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
			throw new ConstructionException(e);
		}
		this.filter = filter;
		setLayout(new BorderLayout());
		setOriginClass();

	}

	private void setOriginClass() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement stack : stackTrace) {
			if (!stack.getClassName().equals("java.lang.Thread")
					&& !stack.getClassName().equals(this.getClass().getName())) {
				originClass = stack.getClassName() + "projectSelection";
				return;
			}
		}

	}

	@Override
	protected void addElements() {

		projects = FileParser.getProjects();

		JComboBox<IProject> projectList = new JComboBox<>();

		projects.stream().filter(filter).forEach(projectList::addItem);

		scopedPreferenceStore = InstanceScope.INSTANCE.getNode("plugin_base_whatevs");
		int index = scopedPreferenceStore.getInt(originClass, -1);

		Optional<IProject> findFirst = projects.stream().filter(filter).findFirst();
		if (index != 0 && projectList.getItemCount() - 1 >= index) {
			projectList.setSelectedIndex(index);
			activeProject = projectList.getItemAt(index);
		} else if (findFirst.isPresent() && activeProject == null) {
			activeProject = findFirst.get();
			projectList.setSelectedIndex(0);
		} else {
			return;
		}

		add(new CustomElementPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void init() {
				setLayout(new BorderLayout());
			}

			@Override
			protected void addElements() {
				add(projectList, BorderLayout.CENTER);

				JButton button = new JButton();
				button.setIcon(new CustomIcon("refresh.png", this));

				button.addMouseListener(new ImplementedMouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							projectDependentChild = childClass.getConstructor().newInstance();
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
							SystemProperties.print(ex);
							throw new ConstructionException(ex);
						}
						recreate();
					}
				});

				add(button, BorderLayout.LINE_END);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(200, 25);
			}

		}, BorderLayout.PAGE_START);

		if (activeProject != null) {
			projectDependentChild.setProject(activeProject);
			projectList.setSelectedItem(activeProject);
			add(projectDependentChild, BorderLayout.CENTER);

		}
		projectList.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox<?> source = (JComboBox<?>) e.getSource();
		activeProject = (IProject) source.getSelectedItem();
		projectDependentChild.setProject(activeProject);
		scopedPreferenceStore.putInt(originClass, source.getSelectedIndex());
		try {
			scopedPreferenceStore.flush();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
		recreate();

	}

	public CustomElementPanel getProjectDependentChild() {
		return projectDependentChild;
	}

}
