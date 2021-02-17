package views.current;

import java.awt.BorderLayout;
import java.awt.Dimension;

import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class CurrentlyActiveView extends CustomElementPanel {

	private static final long serialVersionUID = -3736015318132031278L;

	CurrentlyActiveView() {
		setLayout(new BorderLayout());
	}

	@Override
	protected void addElements() {

		CurrentProjectsView addingPanel = new CurrentProjectsView();
		addingPanel.setMaximumSize(new Dimension(200, 999999));
		add(addingPanel, BorderLayout.CENTER);

	}

}
