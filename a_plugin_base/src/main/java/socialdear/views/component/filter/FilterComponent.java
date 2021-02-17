package socialdear.views.component.filter;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import socialdear.logging.SystemProperties;
import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public class FilterComponent extends CustomElementPanel implements DocumentListener {

	protected JTextArea textFilter;
	/**
	 * 
	 */
	private static final long serialVersionUID = -882277864664857630L;
	private Class<? extends FilterableComponent<?>> childComponent;
	protected FilterableComponent<?> childComponentInstance;

	public FilterComponent(Class<? extends FilterableComponent<?>> childComponent) {
		this.childComponent = childComponent;
		setLayout(new BorderLayout());
	}

	@Override
	protected void addElements() {
		textFilter = new JTextArea();
		textFilter.getDocument().addDocumentListener(this);
		add(textFilter, BorderLayout.NORTH);

		try {
			childComponentInstance = childComponent.getConstructor().newInstance();
			add(childComponentInstance, BorderLayout.CENTER);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			SystemProperties.print(e);
		}

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		childComponentInstance.setFilter(textFilter.getText());

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		childComponentInstance.setFilter(textFilter.getText());

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		childComponentInstance.setFilter(textFilter.getText());

	}

}
