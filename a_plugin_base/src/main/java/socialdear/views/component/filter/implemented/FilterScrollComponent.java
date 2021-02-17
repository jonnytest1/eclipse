package socialdear.views.component.filter.implemented;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JTextArea;

import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.filter.FilterComponent;

public class FilterScrollComponent<T, I extends CustomElementPanel & FilterChildComponent<T>> extends FilterComponent {

	private transient Collection<T> elements;
	private Class<I> childClass;

	public FilterScrollComponent(Collection<T> elements, Class<I> childClass) {
		super(null);
		this.elements = elements;
		this.childClass = childClass;
	}

	@Override
	protected void addElements() {
		textFilter = new JTextArea();
		textFilter.getDocument().addDocumentListener(this);
		add(textFilter, BorderLayout.NORTH);

		ImplementedFilterable<T, I> implementedFilterable = new ImplementedFilterable<>(childClass);
		implementedFilterable.setList(elements);
		childComponentInstance = implementedFilterable;
		add(childComponentInstance, BorderLayout.CENTER);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4871778156960815254L;

}
