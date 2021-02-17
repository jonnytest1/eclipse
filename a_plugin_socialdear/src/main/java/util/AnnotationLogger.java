package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.logging.SystemProperties;

public class AnnotationLogger {
	private AnnotationLogger() {
		//
	}

	public static void logClassAnnotation(Class<?> tableClass, Annotation annotation, Throwable e) {
		if (annotation.toString().contains("com.fasterxml.jackson")) {
			return;
		}
		if (annotation.toString().contains("javax.persistence.IdClass")) {
			return;
		}
		if (annotation.toString().contains("javax.persistence.Basic")) {
			return;
		}

		String annotationDetails = getAnnotationDetails(annotation);

		Logging.logRequest(" unknown classAnnotation: " + annotation.getClass().toGenericString() + " in "
				+ tableClass.getCanonicalName() + "\n" + annotationDetails, LogLevel.INFO, null, e);

	}

	public static void logMissingANotation(Class<?> tableClass, Annotation annotation, Field field) {

		if (annotation.toString().contains("com.fasterxml.jackson")) {
			return;
		}

		String details = getAnnotationDetails(annotation);
		SystemProperties.print(LogLevel.ERROR,
				tableClass.getName() + " unknown enum case Attribute Annotation \n"
						+ annotation.getClass().toGenericString() + " on field " + field.getName() + " in "
						+ tableClass.getCanonicalName() + "\n" + details);

	}

	private static String getAnnotationDetails(Annotation annotation) {
		Method[] annMethods = annotation.getClass().getMethods();

		StringBuilder details = new StringBuilder("---------methods---------\n");

		for (Method method : annMethods) {
			if (method.getParameterTypes().length == 0) {
				try {
					if (!method.getReturnType().equals(Void.TYPE)) {
						Object obj = method.invoke(annotation);
						details.append(method.getName());
						details.append(" : ");
						details.append(obj.toString());
						details.append("\n");
					}
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e2) {
					SystemProperties.print(LogLevel.INFO, annotation.toString(), e2);
				}
			}
		}
		details.append("---------------fields---------\n");
		for (Field annField : annotation.getClass().getFields()) {
			try {
				Object obj = annField.get(annotation);
				details.append(annField.getName());
				details.append(" : ");
				details.append(obj.toString());
				details.append("\n");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				SystemProperties.print(LogLevel.INFO, e);
			}

		}
		return details.toString();
	}
}
