package control;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @since 2.1
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "targetProcessIntegration"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public Activator() {
		// empty
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setPlugin(this);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		setPlugin(null);
		super.stop(context);
	}

	public static Activator getDefault() {
		return getPlugin();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Activator getPlugin() {
		return plugin;
	}

	public static void setPlugin(Activator plugin) {
		Activator.plugin = plugin;
	}
}
