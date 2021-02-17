package views.migration.components;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomDimension;
import socialdear.views.component.CustomElementPanel;
import views.migration.components.sidebar.SideBarComponent;

public class MainPanel extends CustomElementPanel implements ImplementedMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -540587829405343172L;
	public static MainPanel instance;
	private SideBarComponent sidebar;
	private Composite swtAwtComponent;

	public MainPanel(Composite swtAwtComponent) {
		instance = this;
		this.swtAwtComponent = swtAwtComponent;
		setLayout(new BorderLayout());
		addMouseListener(this);
		recreate();
	}

	@Override
	protected void addElements() {

		sidebar = new SideBarComponent();
		add(sidebar, BorderLayout.LINE_START);
		add(sidebar.getDatabaseComponent(), BorderLayout.CENTER);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 2 && !e.isConsumed()) {
			e.consume();
			recreate();

			resizeElement((int) calculatedSize.getHeight(), (int) calculatedSize.getHeight());
		}

	}

	@Override
	public void resizeElement(int height, int width) {
		calculatedSize = new CustomDimension(width, height);
		sidebar.resizeElement(height, width);
	}

	public Shell getShell() {
		return swtAwtComponent.getShell();
	}
}
