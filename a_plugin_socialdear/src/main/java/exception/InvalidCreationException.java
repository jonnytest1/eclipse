package exception;

import java.lang.reflect.Field;

public class InvalidCreationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Class<? extends Object> creatingClass;
	private final transient Field field;

	final StackTraceElement[] createdBy;

	public InvalidCreationException(Class<? extends Object> creatingClass, Field field) {

		createdBy = Thread.currentThread().getStackTrace();
		this.creatingClass = creatingClass;
		this.field = field;

	}

	@Override
	public String getMessage() {
		return "created details: class :" + creatingClass.getCanonicalName() + " field: " + field.getName();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return createdBy;
	}

}
