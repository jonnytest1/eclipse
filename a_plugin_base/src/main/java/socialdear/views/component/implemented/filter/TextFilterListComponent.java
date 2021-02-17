package socialdear.views.component.implemented.filter;

import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JScrollBar;

import socialdear.views.component.CustomElementPanel;

public class TextFilterListComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1596620431533191155L;
	private String text;
	private List<FilterableComponent> components;
	private JScrollBar horizontalScrollBar;

	public TextFilterListComponent(List<FilterableComponent> components) {
		this.components = components;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		recreate();
	}

	@Override
	protected void addElements() {
		for (FilterableComponent comp : components) {
			if (text == null || comp.shouldShow(text)) {
				add(comp);
			}
		}
	}

	public void setFilter(String text) {
		this.text = text;
		recreate();
	}

	/**
	 * @since 3.0
	 */
	public <T extends FilterableComponent> void append(T c, Comparator<T> comparator) {
		int index = 0;
		for (int i = 0; i < components.size(); i++) {
			index = i;
			if (components.get(i).getClass() == c.getClass() && comparator.compare(c, (T) components.get(i)) < 1) {
				break;
			}
		}
		int posBefore = horizontalScrollBar.getValue();
		components.add(index, c);
		if (text == null || c.shouldShow(text)) {
			try {
				add(c, index);

				horizontalScrollBar.setValue(posBefore);
				revalidate();
				repaint();
			} catch (Exception e) {
				recreate();
			}
		}
	}

	/**
	 * @since 3.0
	 */
	public void setScrollBar(JScrollBar horizontalScrollBar) {
		this.horizontalScrollBar = horizontalScrollBar;

	}

}
