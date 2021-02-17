package exception;

public class CustomInvalidClassException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7007828516519254784L;
	final String message;

	public CustomInvalidClassException(String message, StackTraceElement[] trace) {
		this.message = message;
		Throwable throwable = new Throwable();
		throwable.setStackTrace(trace);
		initCause(throwable);
	}

	public CustomInvalidClassException(String message, Throwable throwable) {
		this.message = message;
		initCause(throwable);
	}

	@Override
	public String getMessage() {
		return message;
	}
}
