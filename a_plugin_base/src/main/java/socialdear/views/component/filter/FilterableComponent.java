package socialdear.views.component.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import socialdear.views.component.CustomElementPanel;

/**
 * @since 2.1
 */
public abstract class FilterableComponent<T> extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3913296226537983551L;
	private String text = "";

	public void setFilter(String text) {
		this.text = text;
		recreate();

	}

	protected List<T> getFilteredList() {
		return getList().stream() //
				.filter(el -> {
					if (text.isBlank()) {
						return true;
					}

					String filterText = Arrays.stream(text.toLowerCase().split("")) //
							.collect(Collectors.joining("(.*)"));
					return getMatcher(el).toLowerCase().matches("(.*)" + filterText + "(.*)");
				}) //
				.collect(Collectors.toList());
	}

	protected abstract Collection<T> getList();

	private String getMatcher(T element) {
		if (element instanceof String) {
			return (String) element;
		}
		throw new InvalidMatcherException("overwrite getMatcher for types that arent a string");
	}

}