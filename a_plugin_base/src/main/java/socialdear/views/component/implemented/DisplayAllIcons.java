package socialdear.views.component.implemented;

import java.lang.reflect.Field;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;

public class DisplayAllIcons extends BaseViewPart {

	@Override
	protected CustomElementPanel createElement() {
		try {
			IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
			Field[] fields = ISharedImages.class.getDeclaredFields();
			for (Field field : fields) {
				Image img = PlatformUI.getWorkbench().getSharedImages().getImage((String) field.get(null));
				Action addItemAction = new Action("icon", ImageDescriptor.createFromImage(img)) {
					@Override
					public void run() {
						// nothing
					}
				};
				addItemAction.setToolTipText(field.getName());
				mgr.add(addItemAction);
			}

			Field[] fields2 = org.eclipse.jdt.ui.ISharedImages.class.getDeclaredFields();
			org.eclipse.jdt.ui.ISharedImages images = JavaUI.getSharedImages();
			for (Field field : fields2) {
				Image image = images.getImage((String) field.get(null));

				Action addItemAction = new Action("run failed", ImageDescriptor.createFromImage(image)) {
					@Override
					public void run() {
						// nothing
					}
				};
				addItemAction.setToolTipText(field.getName());
				mgr.add(addItemAction);
			}

			for (Field field : org.eclipse.debug.ui.IDebugUIConstants.class.getFields()) {

				try {
					Image image = DebugUITools.getImage((String) field.get(null));
					// Image image = PlatformUI.getWorkbench().getSharedImages().getImage((String)
					// field.get(null));
					if (image == null) {
						continue;
					}
					Action addItemAction = new Action("run failed", ImageDescriptor.createFromImage(image)) {
						@Override
						public void run() {
							// nothing
						}

					};
					addItemAction.setToolTipText(field.getName());
					mgr.add(addItemAction);
				} catch (Exception e) {

				}
			}
		} catch (IllegalAccessException e) {

		}

		return new CustomElementPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void addElements() {
				// default
			}
		};
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return null;
	}

}
