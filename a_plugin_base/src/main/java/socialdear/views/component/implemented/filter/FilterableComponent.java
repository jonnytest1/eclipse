package socialdear.views.component.implemented.filter;

import java.util.Arrays;
import java.util.stream.Collectors;

import socialdear.views.component.CustomElementPanel;

public abstract class FilterableComponent extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8235239467166985261L;

	protected String getMatcherString() {
		return toString();
	}

	public boolean shouldShow(String filter) {
		return getMatcherString().toLowerCase().matches(toRegex(filter));
	}

	protected String toRegex(String filter) {
		return "(.*)" + Arrays.stream(filter.toLowerCase().split("")) //
				.collect(Collectors.joining("(.*)")) + "(.*)";
	}

}
