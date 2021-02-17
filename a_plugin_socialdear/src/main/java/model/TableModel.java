package model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import exception.CustomInvalidClassException;
import exception.InvalidCreationException;
import exception.UnnecessarySqlCommandException;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.logging.SystemProperties;
import sql.Column;
import sql.Constraint;
import util.AnnotationLogger;

public class TableModel {

	private String title;
	private List<Column> columns;
	private List<Constraint> constraints = new ArrayList<>();

	private Class<?> tableClass;

	private boolean isEntity;

	private boolean canAdd = true;

	private Constraint constraint;

	private Column column;

	enum fieldAnnotations {
		TRANSIENT(Transient.class), ONE_TO_MANY(OneToMany.class), MANY_TO_ONE(ManyToOne.class),
		GENERATED_VALUE(GeneratedValue.class), ONE_TO_ONE(OneToOne.class), COLUMN(javax.persistence.Column.class),
		JOIN_COLUMN(JoinColumn.class), NOT_NULL(NotNull.class), ENUMERATED(Enumerated.class), BLOB(Lob.class),
		BASIC(Basic.class), ID(Id.class);

		Class<? extends Annotation> str;

		String classAsString;

		private fieldAnnotations(Class<? extends Annotation> a) {
			this.str = a;
		}

		@Override
		public String toString() {
			if (str == null) {
				return classAsString;
			}
			return str.toString();
		}

		public Class<? extends Annotation> getEnumClass() {
			return str;
		}
	}

	public enum ClassAnnotations {
		TABLE(Table.class), ENTITY(Entity.class),
		JSON_APPEND("interface com.fasterxml.jackson.databind.annotation.JsonAppend"),
		ID_CLASS("interface javax.persistence.IdClass");

		Class<? extends Annotation> str;

		String classAsString;

		private ClassAnnotations(Class<? extends Annotation> a) {
			this.str = a;
		}

		private ClassAnnotations(String a) {
			this.classAsString = a;
		}

		@Override
		public String toString() {
			if (str == null) {
				return classAsString;
			}
			return str.toString();
		}

		public Class<? extends Annotation> getEnumClass() {
			return str;
		}
	}

	public TableModel(String title, List<Column> columns, List<Constraint> constraints) {
		this.title = title;
		this.columns = columns;
		this.constraints = constraints;

	}

	public <T> TableModel(Class<T> tableClass) throws IllegalAccessException, InvocationTargetException {
		this.tableClass = tableClass;
		setEntity(false);

		parseClassAnnotations();

		if (getTitle() == null) {
			setTitle(tableClass.getSimpleName());
		}

		for (Field field : tableClass.getDeclaredFields()) {
			column = new Column(new InvalidCreationException(tableClass, field));

			constraint = null;
			for (Annotation annotation : field.getAnnotations()) {
				parseAnnotation(annotation, field);
			}

			column.setColumnName(field.getName());

			column.setType(field.getType().toString());

			if (constraint != null) {
				getConstraints().add(constraint);
			}
			if (canAdd) {
				getColumns().add(column);
			}
		}

	}

	void parseAnnotation(Annotation annotation, Field field) throws IllegalAccessException, InvocationTargetException {
		try {
			fieldAnnotations anno = getEnumFromClass(annotation.annotationType(), fieldAnnotations.class);

			switch (anno) {
			case TRANSIENT:
				canAdd = false;
				break;
			case ID:
				column.setPrimaryKey(true);
				column.setNullAllowed(false);
				break;
			case BLOB:
				column.setType("BLOB");
				break;
			case GENERATED_VALUE:
				column.setDefaultValue("AUTO INCREMENT");
				break;
			case COLUMN:
				parseColumn(annotation);
				break;
			case NOT_NULL:
				column.setNullAllowed(false);
				break;
			case MANY_TO_ONE:
				parseManyToOne(annotation);
				break;
			case ONE_TO_MANY:
				parseOneToMany(annotation);
				break;
			case JOIN_COLUMN:
				parseJoinColumn(annotation, field);
				break;
			case ENUMERATED:
				parseEnumerated(annotation);
				break;
			default:
				AnnotationLogger.logMissingANotation(tableClass, annotation, field);
			}
		} catch (IllegalArgumentException e) {
			AnnotationLogger.logMissingANotation(tableClass, annotation, field);
		}
	}

	private void parseColumn(Annotation annotation) throws IllegalAccessException, InvocationTargetException {
		try {
			String columnName = (String) annotation.annotationType().getMethod("name").invoke(annotation);
			if (!columnName.isEmpty()) {
				column.setColumnName(columnName);
			}
		} catch (NoSuchMethodException e) {
			SystemProperties.printInfo(e);
		}
	}

	private void parseEnumerated(Annotation annotation) throws IllegalAccessException, InvocationTargetException {
		try {
			String type = annotation.annotationType().getMethod("value").invoke(annotation).toString();
			switch (type) {
			case "STRING":
				column.setType("java.lang.String");
				break;
			case "ORDINAL":
				column.setType("int");
				break;
			default:
				break;
			}
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
	}

	private void parseJoinColumn(Annotation annotation, Field field)
			throws IllegalAccessException, InvocationTargetException {
		if (constraint == null) {
			constraint = new Constraint();
		}
		try {
			String referencedColumn = (String) annotation.annotationType().getMethod("referencedColumnName")
					.invoke(annotation);
			if (referencedColumn == null) {
				List<Field> referencedFileds = Arrays.asList(field.getType().getClass().getFields());
				referencedFileds.removeIf(referencedField -> !referencedField.isAnnotationPresent(Id.class));
				if (!referencedFileds.isEmpty()) {
					referencedColumn = referencedFileds.get(0).getName();
				}
			}
			constraint.setConstraintName(referencedColumn);
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
		try {
			String referencedTable = (String) annotation.annotationType().getMethod("table").invoke(annotation);
			if (referencedTable != null && !referencedTable.isEmpty()) {
				constraint.setReferencedTable(referencedTable);
			}
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
		try {
			String constriantNmae = (String) annotation.annotationType().getMethod("name").invoke(annotation);
			constraint.setReferencedColumn(constriantNmae);
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
		try {
			String ownColumn = (String) annotation.annotationType().getMethod("columnDefinition").invoke(annotation);
			constraint.setOwnColumn(ownColumn);
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
		if (constraint.getReferencedTable() == null) {
			if (!"java.util.List".equals(field.getType().getCanonicalName())
					&& !"java.util.Set".equals(field.getType().getCanonicalName())) {
				constraint.setReferencedTable(field.getType().getCanonicalName());
			} else {
				constraint.setReferencedTable(field.getGenericType().getTypeName().split("<")[1].split(">")[0]);
			}

		}
	}

	private void parseOneToMany(Annotation annotation) throws IllegalAccessException, InvocationTargetException {
		if (constraint == null) {
			constraint = new Constraint();
		}
		try {
			Class<?> targetClass = (Class<?>) annotation.annotationType().getMethod("targetEntity").invoke(annotation);
			if (targetClass != null) {
				String targetName = new TableModel(targetClass).getTitle();
				if (targetName.equals("void")) {
					constraint.setReferencedTable(targetName);
				}
			}
		} catch (NoSuchMethodException e1) {
			SystemProperties.print(LogLevel.INFO, e1);
		}
		try {
			String referencedColumn = (String) annotation.annotationType().getMethod("mappedBy").invoke(annotation);
			constraint.setReferencedColumn(referencedColumn);
		} catch (NoSuchMethodException e) {
			SystemProperties.print(LogLevel.INFO, e);
		}
	}

	private void parseManyToOne(Annotation annotation) throws IllegalAccessException, InvocationTargetException {
		if (constraint == null) {
			constraint = new Constraint();
		}
		try {
			Class<?> targetClass = (Class<?>) annotation.annotationType().getMethod("targetEntity").invoke(annotation);
			String targetName = new TableModel(targetClass).getTitle();
			constraint.setReferencedTable(targetName);
		} catch (NoSuchMethodException e1) {
			SystemProperties.print(LogLevel.INFO, e1);
		}
	}

	void parseClassAnnotations() throws IllegalAccessException, InvocationTargetException {
		for (Annotation annotation : tableClass.getDeclaredAnnotations()) {
			try {
				ClassAnnotations anno = getEnumFromClass(annotation.annotationType(), ClassAnnotations.class);
				switch (anno) {
				case TABLE:
					classParseTable(annotation);
					break;
				case ENTITY:
					setEntity(true);
					break;
				default:
					AnnotationLogger.logClassAnnotation(tableClass, annotation, null);

				}
			} catch (IllegalArgumentException e) {
				AnnotationLogger.logClassAnnotation(tableClass, annotation, e);

			}
		}
	}

	private void classParseTable(Annotation annotation) throws IllegalAccessException, InvocationTargetException {
		try {
			setTitle((String) annotation.annotationType().getMethod("name").invoke(annotation));
		} catch (NoSuchMethodException e) {
			SystemProperties.printInfo(e);
		}
	}

	public static <T extends Enum<T>> T getEnumFromClass(Class<? extends Annotation> annotationType, Class<T> class1) {
		for (T t : EnumSet.allOf(class1)) {
			if (t.toString().equals(annotationType.toString())) {
				return t;
			}
		}

		throw new IllegalArgumentException(
				"didnt find enum for " + annotationType.toString() + " in class " + class1.toString());

	}

	public TableModel() {
		columns = new ArrayList<>();
		constraints = new ArrayList<>();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public boolean isEntity() {
		return isEntity;
	}

	public void setEntity(boolean isEntity) {
		this.isEntity = isEntity;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public String getTitle() {
		return title;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	@Override
	public String toString() {
		return title + " [ " + columns.toString() + " ]";
	}

	public String createSql() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE `");
		stringBuilder.append(title);
		stringBuilder.append("` ( \n");

		for (int i = 0; i < columns.size(); i++) {

			try {
				stringBuilder.append(columns.get(i).toSQL());
			} catch (UnnecessarySqlCommandException | CustomInvalidClassException e) {
				// skip this one
			}
			if (i != columns.size() - 1) {
				stringBuilder.append(" , \n");
			}

		}
		stringBuilder.append(" ) ;");
		return stringBuilder.toString();

	}

}
