package it.moneyverse.test.utils.helper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.platform.commons.util.ReflectionUtils;

public class ScriptHelper {

  public static final String SQL_SCRIPT_FILE_NAME = "script.sql";

  public static <T, E extends T> void saveScriptFile(Path dir, List<T> entities, Class<E> clazz) {
    String script = generateScript(entities, clazz);
    Path sqlFile = dir.resolve(SQL_SCRIPT_FILE_NAME);
    try {
      Files.write(sqlFile, script.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write script file", e);
    }
  }

  private static <T, E extends T> String generateScript(List<T> entities, Class<E> clazz) {
    if (!clazz.isAnnotationPresent(Entity.class) || !clazz.isAnnotationPresent(Table.class)) {
      throw new IllegalArgumentException(
              "The class " + clazz.getName() + " is not annotated with @Entity and @Table");
    }

    StringBuilder script = new StringBuilder();
    String tableName = getTableName(clazz);

    for (T entity : entities) {
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      Class<?> currentClass = clazz;
      while (currentClass != null && currentClass != Object.class) {
        processFieldsForEntity(currentClass, entity, columns, values);

        currentClass = currentClass.getSuperclass();
      }

      script.append(createInsertRow(tableName, columns, values));
    }
    return script.toString();
  }

  private static String getTableName(Class<?> clazz) {
    return clazz.getAnnotation(Table.class).name();
  }

  private static <T> void processFieldsForEntity(Class<?> currentClass, T entity, StringBuilder columns, StringBuilder values) {
    for (Field field : currentClass.getDeclaredFields()) {
      ReflectionUtils.makeAccessible(field);
      if (field.isAnnotationPresent(Column.class)) {
        ReflectionUtils.makeAccessible(field);
        appendColumnAndValue(field, entity, columns, values);
      }
    }
  }

  private static <T> void appendColumnAndValue(Field field, T entity, StringBuilder columns, StringBuilder values) {
    if (!columns.isEmpty()) {
      columns.append(", ");
      values.append(", ");
    }
    columns.append(getColumnName(field));

    Object value = getFieldValue(field, entity);
    appendValue(value, values);
  }

  private static String getColumnName(Field field) {
    return field.getAnnotation(Column.class).name();
  }

  private static <T> Object getFieldValue(Field field, T entity) {
    try {
      return field.get(entity);
    } catch (IllegalAccessException ex) {
      throw new IllegalArgumentException("The field " + field.getName() + " is not accessible");
    }
  }

  private static void appendValue(Object value, StringBuilder values) {
    switch (value) {
      case null -> values.append("NULL");
      case String s -> values.append("'").append(escapeString(s)).append("'");
      case Enum<?> ignored -> values.append("'").append(value).append("'");
      case UUID ignored -> values.append("'").append(value).append("'");
      case LocalDateTime ignored -> values.append("'").append(value).append("'");
      default -> values.append(value);
    }
  }

  private static String escapeString(String value) {
    return value.replace("'", "''");
  }

  private static String createInsertRow(String tableName, StringBuilder columns, StringBuilder values) {
    return "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString()
        + ");" + "\n";
  }

  private ScriptHelper() {}

}
