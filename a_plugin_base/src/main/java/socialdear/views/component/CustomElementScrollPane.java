package socialdear.views.component;

import java.awt.ScrollPane;

public abstract class CustomElementScrollPane extends ScrollPane {

	public CustomDimension calculatedSize;

	private static final long serialVersionUID = 1L;

	protected abstract void addElements();

	public void recreate() {
		removeAll();
		addElements();
		revalidate();
		repaint();
	}

	public void resizeElement(int height, int width) {
		calculatedSize = new CustomDimension(width, height);
		recreate();
	}

	public CustomDimension getElementSize() {
		return calculatedSize;
	}

}