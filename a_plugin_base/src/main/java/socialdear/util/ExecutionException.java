package socialdear.util;

public class ExecutionException extends Exception {

	private final String text;
	private final String error;

	public ExecutionException(String text, String error) {
		this.text = text;
		this.error = error;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3473729068889521195L;

	public String getText() {
		return text;
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return text + "\n" + error;
	}
}
