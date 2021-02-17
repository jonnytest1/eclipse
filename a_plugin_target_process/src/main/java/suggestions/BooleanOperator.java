package suggestions;

import socialdear.views.component.autocomplete.ComparableText;

/**
 * @since 2.1
 */
public class BooleanOperator extends ComparableText {

	public BooleanOperator(String keyword) {
		super(keyword);
	}

	@Override
	public boolean canBeNExt(ComparableText other) {
		if (other instanceof BooleanOperator) {
			return false;
		} else if (other instanceof AttributeOperator) {
			return true;
		}
		return true;
	}

}
