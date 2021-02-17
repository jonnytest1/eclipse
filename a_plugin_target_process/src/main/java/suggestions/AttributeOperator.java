package suggestions;

import socialdear.views.component.autocomplete.ComparableText;

/**
 * @since 2.1
 */
public class AttributeOperator extends ComparableText {

	protected KeywordOperator parent;

	public AttributeOperator(String keyword, KeywordOperator parent) {
		super("." + keyword);
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
