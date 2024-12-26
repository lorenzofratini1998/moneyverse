package it.moneyverse.test.services;

import jakarta.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.platform.commons.util.ReflectionUtils;

public class SQLScriptService implements ScriptService {

  private static final String INSERT_INTO_ROW_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";

  @Override
  public <T> String createInsertScript(List<T> entities) {
    return entities.stream().map(this::createInsertRow).collect(Collectors.joining("\n"));
  }

  private <T> String createInsertRow(T entity) {
    Class<?> clazz = entity.getClass();
    if (!clazz.isAnnotationPresent(Entity.class) || !clazz.isAnnotationPresent(Table.class)) {
      throw new IllegalArgumentException(
          "The class " + clazz.getName() + " is not annotated with @Entity and @Table");
    }
    String tableName = getTableName(clazz);
    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();
    Class<?> currentClass = clazz;
    while (currentClass != null && currentClass != Object.class) {
      processFieldsForEntity(currentClass, entity, columns, values);

      currentClass = currentClass.getSuperclass();
    }
    return INSERT_INTO_ROW_TEMPLATE.formatted(tableName, columns, values);
  }

  private <T> void processFieldsForEntity(
      Class<?> currentClass, T entity, StringBuilder columns, StringBuilder values) {
    for (Field field : currentClass.getDeclaredFields()) {
      ReflectionUtils.makeAccessible(field);
      if (field.isAnnotationPresent(Column.class)) {
        appendColumnAndValue(field, entity, columns, values);
      } else if (field.isAnnotationPresent(JoinColumn.class)) {
        appendJoinColumnAndValue(field, entity, columns, values);
      }
    }
  }

  private <T> void appendColumnAndValue(
      Field field, T entity, StringBuilder columns, StringBuilder values) {
    if (!columns.isEmpty()) {
      columns.append(", ");
      values.append(", ");
    }
    columns.append(getColumnName(field));

    Object value = getFieldValue(field, entity);
    appendValue(value, values);
  }

  private String getColumnName(Field field) {
    return field.getAnnotation(Column.class).name();
  }

  private <T> void appendJoinColumnAndValue(
      Field field, T entity, StringBuilder columns, StringBuilder values) {
    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
    if (!columns.isEmpty()) {
      columns.append(", ");
      values.append(", ");
    }
    columns.append(joinColumn.name());
    Object relatedEntity = getFieldValue(field, entity);
    Object foreignKeyValue = extractForeignKeyValue(relatedEntity);

    appendValue(foreignKeyValue, values);
  }

  private Object extractForeignKeyValue(Object relatedEntity) {
    if (relatedEntity == null) {
      return null;
    }
    for (Field relatedField : relatedEntity.getClass().getDeclaredFields()) {
      if (relatedField.isAnnotationPresent(Id.class)) {
        ReflectionUtils.makeAccessible(relatedField);
        return getFieldValue(relatedField, relatedEntity); // Return the ID value
      }
    }
    throw new IllegalArgumentException(
        "The related entity "
            + relatedEntity.getClass().getName()
            + " does not have a field annotated with @Id");
  }

  private <T> Object getFieldValue(Field field, T entity) {
    try {
      return field.get(entity);
    } catch (IllegalAccessException ex) {
      throw new IllegalArgumentException("The field " + field.getName() + " is not accessible");
    }
  }

  private void appendValue(Object value, StringBuilder values) {
    switch (value) {
      case null -> values.append("NULL");
      case String s -> values.append("'").append(escapeString(s)).append("'");
      case Enum<?> ignored -> values.append("'").append(value).append("'");
      case UUID ignored -> values.append("'").append(value).append("'");
      case LocalDateTime ignored -> values.append("'").append(value).append("'");
      default -> values.append(value);
    }
  }

  private String escapeString(String value) {
    return value.replace("'", "''");
  }

  private String getTableName(Class<?> clazz) {
    return clazz.getAnnotation(Table.class).name();
  }
}
