package a_plugin_struts.service;

public class ConverterException extends RuntimeException {

	public ConverterException(String string) {
		super(string);
	}

	public ConverterException(String string, Exception e) {
		super(string, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642977764510002262L;

}
