package it.moneyverse.test.services;

import jakarta.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    StringBuilder manyToManyScript = new StringBuilder();

    Class<?> currentClass = clazz;
    while (currentClass != null && currentClass != Object.class) {
      processFieldsForEntity(currentClass, entity, columns, values, manyToManyScript);

      currentClass = currentClass.getSuperclass();
    }
    String entityScript = INSERT_INTO_ROW_TEMPLATE.formatted(tableName, columns, values);
    return entityScript + "\n" + manyToManyScript;
  }

  private <T> void processFieldsForEntity(
      Class<?> currentClass,
      T entity,
      StringBuilder columns,
      StringBuilder values,
      StringBuilder manyToManyScript) {
    for (Field field : currentClass.getDeclaredFields()) {
      handleField(field, entity, columns, values, manyToManyScript);
    }
  }

  private <T> void handleField(
      Field field,
      T entity,
      StringBuilder columns,
      StringBuilder values,
      StringBuilder manyToManyScript) {

    ReflectionUtils.makeAccessible(field);

    if (tryColumn(field, entity, columns, values)) {
      return;
    }
    if (tryJoinColumn(field, entity, columns, values)) {
      return;
    }
    if (tryManyToMany(field, entity, manyToManyScript)) {
      return;
    }
    tryEmbedded(field, entity, columns, values);
  }

  private <T> boolean tryColumn(
      Field field, T entity, StringBuilder columns, StringBuilder values) {

    if (!field.isAnnotationPresent(Column.class)) {
      return false;
    }
    appendColumnAndValue(field, entity, columns, values);
    return true;
  }

  private <T> boolean tryJoinColumn(
      Field field, T entity, StringBuilder columns, StringBuilder values) {

    if (!field.isAnnotationPresent(JoinColumn.class)) {
      return false;
    }
    appendJoinColumnAndValue(field, entity, columns, values);
    return true;
  }

  private <T> boolean tryManyToMany(Field field, T entity, StringBuilder manyToManyScript) {

    if (!(field.isAnnotationPresent(ManyToMany.class)
        && field.isAnnotationPresent(JoinTable.class))) {
      return false;
    }
    appendManyToManyScript(field, entity, manyToManyScript);
    return true;
  }

  private <T> void tryEmbedded(Field field, T entity, StringBuilder columns, StringBuilder values) {

    if (!field.isAnnotationPresent(Embedded.class)) {
      return;
    }

    Object embeddable = getFieldValue(field, entity);
    if (embeddable == null) {
      return;
    }

    for (Field embField : embeddable.getClass().getDeclaredFields()) {
      ReflectionUtils.makeAccessible(embField);
      if (embField.isAnnotationPresent(Column.class)) {
        appendColumnAndValue(embField, embeddable, columns, values);
      }
    }
  }

  private <T> void appendColumnAndValue(
      Field field, T entity, StringBuilder columns, StringBuilder values) {
    Object value = getFieldValue(field, entity);
    if (value != null) {
      if (!columns.isEmpty()) {
        columns.append(", ");
        values.append(", ");
      }
      columns.append(getColumnName(field));
      appendValue(value, values);
    }
  }

  private String getColumnName(Field field) {
    return field.getAnnotation(Column.class).name();
  }

  private <T> void appendJoinColumnAndValue(
      Field field, T entity, StringBuilder columns, StringBuilder values) {
    Object relatedEntity = getFieldValue(field, entity);
    Object foreignKeyValue = extractForeignKeyValue(relatedEntity);
    if (foreignKeyValue != null) {
      JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
      if (!columns.isEmpty()) {
        columns.append(", ");
        values.append(", ");
      }
      columns.append(joinColumn.name());
      appendValue(foreignKeyValue, values);
    }
  }

  private <T> void appendManyToManyScript(Field field, T entity, StringBuilder manyToManyScript) {
    JoinTable joinTable = field.getAnnotation(JoinTable.class);

    String joinTableName = joinTable.name();
    String joinColumnName = joinTable.joinColumns()[0].name();
    String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();

    Object entityId = extractForeignKeyValue(entity);
    Set<?> relatedEntities = (Set<?>) getFieldValue(field, entity);

    for (Object relatedEntity : relatedEntities) {
      Object relatedEntityId = extractForeignKeyValue(relatedEntity);
      manyToManyScript
          .append(
              INSERT_INTO_ROW_TEMPLATE.formatted(
                  joinTableName,
                  joinColumnName + ", " + inverseJoinColumnName,
                  formatValues(entityId, relatedEntityId)))
          .append("\n");
    }
  }

  private String formatValues(Object... values) {
    StringBuilder formattedValues = new StringBuilder();
    for (Object value : values) {
      if (!formattedValues.isEmpty()) {
        formattedValues.append(", ");
      }
      StringBuilder singleValue = new StringBuilder();
      appendValue(value, singleValue);
      formattedValues.append(singleValue);
    }
    return formattedValues.toString();
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
    if (value == null) {
      values.append("NULL");
      return;
    }

    if (value instanceof String
        || value instanceof Enum
        || value instanceof UUID
        || value instanceof LocalDateTime
        || value instanceof LocalDate
        || value instanceof Character) {
      values.append("'").append(escapeString(value.toString())).append("'");
    } else if (value instanceof Number || value instanceof Boolean) {
      values.append(value);
    } else {
      throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getName());
    }
  }

  private String escapeString(String value) {
    return value.replace("'", "''");
  }

  private String getTableName(Class<?> clazz) {
    return clazz.getAnnotation(Table.class).name();
  }
}
