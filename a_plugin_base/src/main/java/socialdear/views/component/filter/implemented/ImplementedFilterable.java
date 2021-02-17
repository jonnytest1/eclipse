package socialdear.views.component.filter.implemented;

import java.awt.BorderLayout;
import java.util.Collection;

import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.filter.FilterableComponent;

public class ImplementedFilterable<T, I extends CustomElementPanel & FilterChildComponent<T>>
		extends FilterableComponent<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7158512943367023099L;

	private transient Collection<T> elements;

	private Class<I> childCass;

	public ImplementedFilterable(Class<I> childCass) {
		this.childCass = childCass;
		setLayout(new BorderLayout());
	}

	@Override
	protected Collection<T> getList() {
		return elements;
	}

	public void setList(Collection<T> elements) {
		this.elements = elements;
		recreate();
	}

	@Override
	protected void addElements() {
		add(getScrollPane(new FilterListView<T, I>(getFilteredList(), childCass)), BorderLayout.CENTER);
	}

}
