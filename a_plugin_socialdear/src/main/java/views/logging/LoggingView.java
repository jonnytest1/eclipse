package views.logging;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import socialdear.views.JPanelProvider;

public class LoggingView extends ViewPart {

	@Override
	public void createPartControl(Composite arg0) {

		JPanelProvider.addJPanel(new LoggingElementView(), arg0);
	}

	@Override
	public void setFocus() {
	}

}
