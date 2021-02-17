package a_plugin_struts.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoxLayout;

import org.eclipse.core.resources.IProject;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.model.ActionBeanObject;
import a_plugin_struts.model.FilesContainer;
import a_plugin_struts.model.FormBeanObject;
import a_plugin_struts.model.ForwardObject;
import a_plugin_struts.model.StrutsObjectFactory;
import a_plugin_struts.views.strutsviews.StrutsActionView;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;
import socialdear.views.component.implemented.ProjectSelectionReceiver;
import socialdear.views.component.implemented.filter.FilterableComponent;
import socialdear.views.component.implemented.filter.TextFilterCoponent;

public class StrutsProjectPage extends ProjectSelectionReceiver {

	private FilesContainer files = new FilesContainer();

	public static boolean finishedParsingFiles = false;

	private transient List<ActionBeanObject> actions = new CopyOnWriteArrayList<>();
	private transient Map<String, FormBeanObject> forms = new ConcurrentHashMap<>();
	private transient List<ForwardObject> forwards = new CopyOnWriteArrayList<>();

	public StrutsProjectPage() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	public void setProject(IProject project) {
		if (project != null) {
			actionForwards = new ConcurrentHashMap<>();
			new Thread(() -> {
				finishedParsingFiles = false;
				FileParser.getAllFiles(project, StrutsProperties.getFileBlackList()).parallelStream() //
						.forEach(file -> {
							if (file.getName().endsWith("xml")) {
								files.addXml(file);
							} else if (file.getName().endsWith("java")) {
								files.addJava(file);
							} else if (file.getName().endsWith("xsl")) {
								files.addXsl(file);
							}
						});
				parseStrutsFiles(project);
			}).start();

		}
		recreate();

	}

	private void parseStrutsFiles(IProject project2) {
		actions = new CopyOnWriteArrayList<>();
		forwards = new CopyOnWriteArrayList<>();
		forms.clear();
		SystemProperties.print("finished listing files - filessize " + files.getSize());
		files.getXmlFiles().parallelStream()//
				.filter(file -> file.getName().matches("(.*)struts-(.*)xml")) //
				.flatMap(file -> StrutsObjectFactory.parse(file).stream()) //
				.forEach(strutsobject -> {
					strutsobject.setProject(project2);
					if (strutsobject instanceof ActionBeanObject) {
						ActionBeanObject ac = (ActionBeanObject) strutsobject;
						actions.add(ac);

						boolean wasNull = false;
						if (actionForwards.get(ac.getPath()) == null) {
							wasNull = true;

						}
						actionForwards.computeIfAbsent(ac.getPath(), k -> new ArrayList<>()).add(ac);
						if (wasNull) {
							filterComponentent.append(new StrutsActionView(actionForwards.get(ac.getPath()), files), (v1, v2) -> v1.getActionobj().getPath().compareTo(v2.getActionobj().getPath()));
						}

					} else if (strutsobject instanceof FormBeanObject) {
						FormBeanObject form = (FormBeanObject) strutsobject;
						forms.put(form.getName(), form);
					} else if (strutsobject instanceof ForwardObject) {
						forwards.add((ForwardObject) strutsobject);
					}
				});
		SystemProperties.print("finished parsing actionssize " + actions.size() + " formssize " + forms.entrySet().size());
		SystemProperties.print("forwards: " + forwards.size());

		matchForwards();
		files.getJavaFiles().parallelStream().forEach(file -> {
			String fileStr = project2.getLocationURI().relativize(file.toURI()).toString();
			forms.forEach((name, form) -> {
				if (fileStr.matches(form.getFileRegex())) {
					form.addSourceFile(file);
				}
			});
			actions.forEach(action -> {
				if (action.getFormName() == null) {
					return;
				}
				action.setForm(forms.get(action.getFormName()));
				if (fileStr.matches(action.getFileRegex())) {
					action.addSourceFile(file);
				}
			});
		});
		SystemProperties.print("finished matching files");
		finishedParsingFiles = true;

	}

	private void matchForwards() {
		forwards.parallelStream().forEach(forward -> {
			if (forward.getPath() == null || !forward.getPath().split("\\?")[0].endsWith(".do")) {
				return;
			}
			String matchPath = forward.getPath().split("\\?")[0].replace(".do", "");

			List<ActionBeanObject> fActions = actionForwards.get(matchPath);
			if (fActions != null) {
				forward.setAction(fActions);
			} else {
				int t = 5;
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3231354048525304495L;

	private Map<String, List<ActionBeanObject>> actionForwards = new ConcurrentHashMap<>();;

	private TextFilterCoponent filterComponentent;

	@Override
	protected void addElements() {

		List<FilterableComponent> entries = new ArrayList<>();

		if (actions != null) {
			actionForwards.keySet().stream() //
					.sorted((p1, p2) -> p1.compareTo(p2))//
					.map(path -> new StrutsActionView(actionForwards.get(path), files)) //
					.forEach(entries::add);

			filterComponentent = new TextFilterCoponent(entries);
			add(filterComponentent);
			/*actions.stream() //
					.sorted() //
					.map(o -> new StrutsActionView(o, files)) //*/
		}

	}

}
