package socialdear.views.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import socialdear.views.BaseViewPart;

/**
 * 1.2
 */
public abstract class CustomElementPanel extends CustomPanel {

	private transient BaseViewPart viewPArt;

	protected transient CustomDimension calculatedSize;

	private static final long serialVersionUID = 1L;

	protected abstract void addElements();

	private boolean init = false;

	protected CustomElementPanel() {

	}

	protected void cleanup() {

	}

	public void cleanupComponent() {
		for (Component c : getComponents()) {
			if (c instanceof CustomElementPanel) {
				((CustomElementPanel) c).cleanupComponent();
			}
		}
		cleanup();
	}

	public void recreate() {
		removeAll();
		try {
			init();
			addElements();
		} catch (Exception e) {
			e.printStackTrace();
		}
		init = true;
		revalidate();
		repaint();
	}

	@Override
	public void removeAll() {
		for (Component c : getComponents()) {
			if (c instanceof CustomElementPanel) {
				((CustomElementPanel) c).cleanupComponent();
			}
		}
		super.removeAll();
	}

	@Override
	public void remove(Component c) {
		if (c instanceof CustomElementPanel) {
			((CustomElementPanel) c).cleanupComponent();
		}
		super.remove(c);
	}

	@Override
	public void remove(int arg0) {
		Component c = getComponent(arg0);
		if (c instanceof CustomElementPanel) {
			((CustomElementPanel) c).cleanupComponent();
		}
		super.remove(arg0);
	}

	@Override
	public void doLayout() {
		if (!init) {
			init = true;
			try {
				init();
				addElements();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.doLayout();
	}

	protected void init() {

	}

	public JScrollPane getScrollPane(JPanel addingPanel) {

		JScrollPane scrollPane = new JScrollPane(addingPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		scrollPane.setAlignmentY(TOP_ALIGNMENT);
		scrollPane.setVisible(true);
		scrollPane.setOpaque(false);
		scrollPane.setBackground(null);
		scrollPane.setBorder(null);
		scrollPane.revalidate();
		return scrollPane;

	}

	public JPanel withLeftPadding(int padding) {

		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
		containerPanel.setLayout(new BorderLayout());

		containerPanel.add(this, BorderLayout.CENTER);

		return containerPanel;

	}

	protected JScrollPane findScroolPane() {
		return findParent(JScrollPane.class);
	}

	public void resizeElement(int height, int width) {
		calculatedSize = new CustomDimension(width, height);
	}

	public CustomDimension getElementSize() {
		return calculatedSize;
	}

	@SuppressWarnings("unchecked")
	protected <T> T findParent(Class<? extends T> spec) {
		Container c = this;
		while (true) {
			if (c.getClass().isAssignableFrom(spec)) {
				return (T) c;
			} else if (c.getParent() == null) {
				return null;
			} else {
				c = c.getParent();
			}
		}
	}

	/**
	 * @since 3.0
	 */
	public JLabel addButton(String text, java.awt.event.MouseListener m) {
		JLabel b = new JLabel(text);
		b.addMouseListener(m);
		add(b);
		return b;
	}

	public BaseViewPart getRootView() {
		if (viewPArt == null) {
			return ((CustomElementPanel) getParent()).getRootView();
		} else {
			return viewPArt;
		}
	}

	public void setViewPArt(BaseViewPart baseViewPart) {
		viewPArt = baseViewPart;

	}
}
