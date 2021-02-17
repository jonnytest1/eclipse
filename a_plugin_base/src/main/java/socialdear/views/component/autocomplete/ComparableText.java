package socialdear.views.component.autocomplete;

public class ComparableText implements Comparable<ComparableText> {

	public final String keyword;

	public ComparableText(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean canBeNExt(ComparableText other) {
		return true;
	}

	@Override
	public int compareTo(ComparableText o) {
		return keyword.compareTo(o.keyword);
	}
}
