package it.moneyverse.test.annotations.datasource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class CleanDatabaseExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    cleanAndMigrate(context);
    populateDatabase(context);
  }

  private void cleanAndMigrate(ExtensionContext context) {
    Flyway flyway = SpringExtension.getApplicationContext(context).getBean(Flyway.class);
    flyway.clean();
    flyway.migrate();
  }

  private void populateDatabase(ExtensionContext context) {
    JdbcTemplate jdbcTemplate = SpringExtension.getApplicationContext(context)
        .getBean(JdbcTemplate.class);
    Path filePath = getAnnotatedFieldValue(context);
    try {
      String[] sqlStatements = Files.readString(filePath).split(";");
      jdbcTemplate.batchUpdate(sqlStatements);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read script file", e);
    }
  }

  private Path getAnnotatedFieldValue(ExtensionContext context) {
    Class<?> currentClass = context.getRequiredTestClass();
    while (currentClass != null) {
      // Try to find the field with the annotation at the current level of the hierarchy
      Optional<Path> fieldValue = Arrays.stream(currentClass.getDeclaredFields())
              .filter(field -> field.isAnnotationPresent(DataSourceScriptDir.class))
              .findFirst()
              .map(field -> {
                ReflectionUtils.makeAccessible(field);
                try {
                  DataSourceScriptDir dirAnnotation = field.getAnnotation(DataSourceScriptDir.class);
                  return ((Path) field.get(context.getRequiredTestClass())).resolve(dirAnnotation.fileName());
                } catch (IllegalAccessException e) {
                  throw new IllegalStateException("Unable to get field data from test instance", e);
                }
              });

      // If the field is found, return its value
      if (fieldValue.isPresent()) {
        return fieldValue.get();
      }

      // Move to the superclass
      currentClass = currentClass.getSuperclass();
    }
    throw new IllegalStateException("%s not found".formatted(DataSourceScriptDir.class.getName()));
  }
}
