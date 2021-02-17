package socialdear.exceptions;

public class NoParentException extends RuntimeException {

	private static final long serialVersionUID = 4182101801712331421L;
	private final String message;

	public NoParentException(String message) {
		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
