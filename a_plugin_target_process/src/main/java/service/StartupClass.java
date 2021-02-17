package service;

import org.eclipse.ui.IStartup;

/**
 * @since 2.1
 */
public class StartupClass implements IStartup {

	@Override
	public void earlyStartup() {
		new GitListener();
		new SAPListener();
	}
}
