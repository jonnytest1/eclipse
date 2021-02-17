package config;

public class CredentialsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3789566499843256187L;
	private final String text;

	public CredentialsException(String text) {
		this.text = text;
	}

	String getText() {
		return text;
	}

}
