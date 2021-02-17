package socialdear.views.component.filter;

public class InvalidMatcherException extends RuntimeException {

	private String string;

	public InvalidMatcherException(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7537473858714279755L;

}
