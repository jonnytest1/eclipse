package socialdear.exceptions;

import socialdear.http.CustomResponse;

/**
 *  1.3
 */
public class LoggingIpException extends Exception {

	private final transient CustomResponse response;

	public LoggingIpException(CustomResponse response) {
		this.response = response;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return response.getContent();
	}
}
