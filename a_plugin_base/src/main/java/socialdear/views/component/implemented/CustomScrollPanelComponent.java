package socialdear.views.component.implemented;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * @since 2.19.4
 * @author Jonathan
 *
 */
public class CustomScrollPanelComponent extends JScrollPane {

	private static final long serialVersionUID = 759717821003823246L;

	Integer lastMaximum;
	Integer lastBottom;

	public CustomScrollPanelComponent(JPanel addingPanel) {
		super(addingPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().setUnitIncrement(16);
		setAlignmentX(LEFT_ALIGNMENT);
		setAlignmentY(TOP_ALIGNMENT);
		setVisible(true);

		setOpaque(false);
		setBackground(null);
		setBorder(null);
		revalidate();

		getVerticalScrollBar().getModel().addChangeListener(e -> {
			if (e.getSource() instanceof DefaultBoundedRangeModel) {
				DefaultBoundedRangeModel change = (DefaultBoundedRangeModel) e.getSource();

				if (lastMaximum != null && change.getMaximum() != lastMaximum && lastMaximum.equals(lastBottom)
						&& change.getValue() + change.getExtent() != change.getMaximum()) {
					getVerticalScrollBar().setValue(change.getMaximum());
				}
				lastMaximum = change.getMaximum();
				lastBottom = change.getValue() + change.getExtent();
			}

		});
	}
}
