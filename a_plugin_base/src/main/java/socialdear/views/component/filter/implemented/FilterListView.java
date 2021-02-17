package socialdear.views.component.filter.implemented;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.BoxLayout;

import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

public class FilterListView<T, I extends CustomElementPanel & FilterChildComponent<T>> extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3856805735422030850L;

	private transient List<T> filteredList;

	private Class<I> childClass;

	public FilterListView(List<T> filteredList, Class<I> childClass) {
		this.filteredList = filteredList;
		this.childClass = childClass;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	protected void addElements() {
		for (T element : filteredList) {
			try {
				I childComponentInstance = childClass.getConstructor().newInstance();
				childComponentInstance.setElement(element);
				add(childComponentInstance);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				SystemProperties.print(e);
			}
		}

	}

}
