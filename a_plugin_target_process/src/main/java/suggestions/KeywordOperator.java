package suggestions;

import socialdear.views.component.autocomplete.ComparableText;

/**
 * @since 2.1
 */
public class KeywordOperator extends ComparableText {

	public KeywordOperator(String keyword) {
		super(keyword);
	}

	@Override
	public boolean canBeNExt(ComparableText other) {
		if (other instanceof BooleanOperator) {
			return true;
		}
		if (other instanceof AttributeOperator) {
			return ((AttributeOperator) other).parent == this;
		}
		return false;
	}

}
