package socialdear.exceptions;

import socialdear.logging.SystemProperties;

/**
 * 1.2
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		SystemProperties.print("\n" + e.getMessage(), e);
	}

}
