package it.moneyverse.test.annotations.datasource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class CleanDatabaseExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
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
    Path dir = getAnnotatedFieldValue(context, DataSourceScriptDir.class, Path.class);
    try {
      String[] sqlStatements = Files.readString(dir.resolve("script.sql")).split(";");
      jdbcTemplate.batchUpdate(sqlStatements);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read script file", e);
    }
  }

  private <T> T getAnnotatedFieldValue(ExtensionContext context,
      Class<? extends Annotation> annotation, Class<T> type) {
    return Arrays.stream(context.getRequiredTestClass().getSuperclass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(annotation))
        .findFirst()
        .map(field -> {
          ReflectionUtils.makeAccessible(field);
          try {
            return type.cast(field.get(context.getRequiredTestClass().getSuperclass()));
          } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to get field data from test instance");
          }
        })
        .orElse(null);
  }
}
