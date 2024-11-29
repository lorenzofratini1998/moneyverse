package it.moneyverse.test.utils.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ReflectionUtils;

public class ExtensionContextHelper {

  /**
   * Retrieves the value of a field annotated with the specified annotation from the test class
   * hierarchy.
   *
   * @param context        the JUnit test context.
   * @param annotationType the class of the annotation to look for.
   * @param valueMapper    a function to map the annotated field to its desired value.
   * @param <T>            the type of the value to return.
   * @return the mapped value of the annotated field.
   */
  public static <T> T getAnnotatedFieldValue(ExtensionContext context,
      Class<? extends Annotation> annotationType,
      FieldValueMapper<T> valueMapper) {
    Class<?> currentClass = context.getRequiredTestClass();

    while (currentClass != null) {
      Optional<T> fieldValue = Arrays.stream(currentClass.getDeclaredFields())
          .filter(field -> field.isAnnotationPresent(annotationType))
          .findFirst()
          .map(field -> {
            ReflectionUtils.makeAccessible(field);
            return valueMapper.map(field, context);
          });

      if (fieldValue.isPresent()) {
        return fieldValue.get();
      }

      currentClass = currentClass.getSuperclass();
    }

    throw new IllegalStateException("%s not found".formatted(annotationType.getName()));
  }

  @FunctionalInterface
  public interface FieldValueMapper<T> {

    T map(Field field, ExtensionContext context);
  }

  /**
   * Retrieves the value of a field of the specified type from the test class hierarchy.
   *
   * @param context    the JUnit test context.
   * @param fieldType  the class of the field type to look for.
   * @param <T>        the type of the field value to return.
   * @return the value of the field if found.
   */
  public static <T> T getFieldOfType(ExtensionContext context, Class<T> fieldType) {
    Class<?> currentClass = context.getRequiredTestClass();

    while (currentClass != null) {
      Optional<T> fieldValue = Arrays.stream(currentClass.getDeclaredFields())
          .filter(field -> fieldType.isAssignableFrom(field.getType()))
          .findFirst()
          .map(field -> {
            ReflectionUtils.makeAccessible(field);
            try {
              return fieldType.cast(field.get(context.getRequiredTestInstance()));
            } catch (IllegalAccessException e) {
              throw new IllegalStateException("Unable to access field of type: " + fieldType.getName(), e);
            }
          });

      if (fieldValue.isPresent()) {
        return fieldValue.get();
      }

      currentClass = currentClass.getSuperclass();
    }

    throw new IllegalStateException("No field of type %s found".formatted(fieldType.getName()));
  }


  private ExtensionContextHelper() {
  }

}
