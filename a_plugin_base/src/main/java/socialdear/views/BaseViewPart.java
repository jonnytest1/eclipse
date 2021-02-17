package socialdear.views;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

import socialdear.exceptions.GlobalExceptionHandler;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public abstract class BaseViewPart extends ViewPart {

	protected Composite intermediate;

	protected CustomElementPanel rootPanel;

	private ImplementedFieldEditorPreferencePage systemInstance;

	public BaseViewPart() {

	}

	protected abstract CustomElementPanel createElement();

	protected void postCreate() {

	}

	@Override
	public void dispose() {
		rootPanel.cleanupComponent();
		super.dispose();
	}

	public abstract Class<? extends ImplementedFieldEditorPreferencePage> getSystem();

	@Override
	public void createPartControl(Composite rootComposite) {
		Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

		try {
			Class<? extends ImplementedFieldEditorPreferencePage> system = getSystem();
			if (system != null) {
				systemInstance = system.getConstructor().newInstance();
				IWorkbenchWindow workbenchWindow = getSite().getPage().getWorkbenchWindow();
				String pageId = systemInstance.checkPreferences();
				if (pageId != null) {
					SystemProperties.displayConfirmDialog(systemInstance.getErrorMessage(),
							"Preferences Missing/Invalid");

					PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(workbenchWindow.getShell(),
							pageId, new String[] {}, null);
					dialog.open();

				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);

		}

		try {
			rootPanel = createElement();
			rootPanel.setViewPArt(this);
			rootPanel.addMouseListener(new ImplementedMouseListener() {

				private Long lastClick = 0L;

				@Override
				public void mouseClicked(MouseEvent e) {
					Long click = System.currentTimeMillis();
					if (click - lastClick < 1000) {
						rootPanel.recreate();
					}
					lastClick = click;
				}
			});
			intermediate = JPanelProvider.addJPanel(rootPanel, rootComposite);

			postCreate();
		} catch (RuntimeException | NoClassDefFoundError e) {
			SystemProperties.print(e);

			throw e;
		}

		if (System.getenv("DEBUG") != null || shouldAddRefreshButton()) {
			addRefreshButton();
		}

	}

	protected boolean shouldAddRefreshButton() {
		return false;
	}

	void addRefreshButton() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		Image img = DebugUITools.getImage(IDebugUIConstants.IMG_ACT_RUN);

		Action addItemAction = new Action("rel", ImageDescriptor.createFromImage(img)) {
			@Override
			public void run() {
				recreate();
			}
		};
		addItemAction.setToolTipText("reload");
		mgr.add(addItemAction);
	}

	protected void recreate() {
		rootPanel.recreate();
	}

	@Override
	public void setFocus() {
		intermediate.setFocus();
	}

	protected ImageDescriptor getImageDescriptor(String relativePath, int size) {

		URL url = this.getClass().getResource("/src/resources/" + relativePath);
		Image image = ImageDescriptor.createFromURL(url).createImage();
		ImageData data = image.getImageData().scaledTo(size, size);

		return ImageDescriptor.createFromImage(new Image(image.getDevice(), data));

	}

	public Shell getShell() {
		return intermediate.getShell();
	}

	public ImplementedFieldEditorPreferencePage getSystemInstance() {
		return systemInstance;
	}

}
