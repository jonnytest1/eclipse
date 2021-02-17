package perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import socialdear.logging.SystemProperties;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class PluginPerspective implements IPerspectiveFactory {

	private static final String TOP_MIDDLE = "topMiddle";
	private static final String LAUNCHER = "launcher";
	private static final String DEBUG = "debug";
	private IPageLayout factory;

	public PluginPerspective() {
		super();
	}

	@Override
	public void createInitialLayout(IPageLayout factory) {
		try {
			this.factory = factory;
			addViews();
			addActionSets();

			// addNewWizardShortcuts();
			// addPerspectiveShortcuts();
			// addViewShortcuts();
		} catch (Exception e) {
			SystemProperties.print(e);
		}
	}

	private void addViews() {
		addLeftFolder();
		addRightFolder();
		getTopMiddle();
		addLauncherFolder();
		addDebugFolder();
	}

	private void addDebugFolder() {
		IFolderLayout debug = factory.createFolder(DEBUG, IPageLayout.RIGHT, 0.5f, TOP_MIDDLE);
		debug.addView("org.eclipse.jdt.debug.ui.DisplayView");

		debug.addView("org.eclipse.debug.ui.ExpressionView");
		debug.addView(IPageLayout.ID_BOOKMARKS);
		debug.addView("org.eclipse.debug.ui.BreakpointView");
		debug.addView("org.eclipse.debug.ui.VariableView");
	}

	private void addLauncherFolder() {
		IFolderLayout launcher = factory.createFolder(LAUNCHER, IPageLayout.LEFT, 0.33f, TOP_MIDDLE);
		launcher.addView("org.eclipse.debug.ui.DebugView");
		launcher.addView("socialdear.launcher");
	}

	void addLeftFolder() {
		IFolderLayout left = factory.createFolder("left", IPageLayout.LEFT, 0.1f, factory.getEditorArea());
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		left.addView("org.eclipse.ui.views.ResourceNavigator");
		left.addView("socialdear.view.currentlyActive");

	}

	void addRightFolder() {
		IFolderLayout right = factory.createFolder("right", IPageLayout.RIGHT, 0.9f, factory.getEditorArea());
		right.addView(IPageLayout.ID_PROBLEM_VIEW);
		right.addView("org.eclipse.team.ccvs.ui.RepositoriesView");
		right.addView("org.eclipse.team.sync.views.SynchronizeView");
		right.addView("org.eclipse.egit.ui.StagingView");
		right.addView("org.eclipse.search.SearchResultView");
	}

	void getTopMiddle() {
		IFolderLayout topFolder = factory.createFolder(TOP_MIDDLE, // NON-NLS-1
				IPageLayout.TOP, 0.25f, factory.getEditorArea());
		topFolder.addView("org.eclipse.jdt.junit.ResultView");
		topFolder.addPlaceholder("org.eclipse.eclemma.ui.CoverageView");
		topFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);

	}

	private void addActionSets() {
		// icons in the top bar

		factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.team.ui.actionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation"); //
		// NON-NLS-1
		// factory.addActionSet(JavaUI.ID_ACTION_SET);
		// factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
	}

	/*
	 * private void addPerspectiveShortcuts() { // probably opening perspectieve for
	 * files
	 * 
	 * factory.addPerspectiveShortcut(
	 * "org.eclipse.team.ui.TeamSynchronizingPerspective"); // NON-NLS-1
	 * factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective"); //
	 * NON-NLS-1
	 * factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective"); //
	 * NON-NLS-1 }
	 * 
	 * private void addNewWizardShortcuts() {
	 * 
	 * // also icons in the top bar //
	 * factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");
	 * // factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //
	 * factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); }
	 * 
	 * private void addViewShortcuts() { // also icons in the top bar
	 * factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView"); // NON-NLS-1
	 * factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView"); //
	 * NON-NLS-1 factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView");
	 * // NON-NLS-1 factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView");
	 * // NON-NLS-1
	 * factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView"); //
	 * NON-NLS-1 // factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
	 * // factory.addShowViewShortcut(JavaUI.ID_PACKAGES); //
	 * factory.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER); //
	 * factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW); //
	 * factory.addShowViewShortcut(IPageLayout.ID_OUTLINE); }
	 */

}
