package it.moneyverse.test.annotations.datasource;

import it.moneyverse.test.utils.helper.ExtensionContextHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
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
    JdbcTemplate jdbcTemplate =
        SpringExtension.getApplicationContext(context).getBean(JdbcTemplate.class);
    Path filePath = getDataSourceScriptDirValue(context);
    try {
      String[] sqlStatements = Files.readString(filePath).split(";");
      jdbcTemplate.batchUpdate(sqlStatements);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read script file", e);
    }
  }

  private Path getDataSourceScriptDirValue(ExtensionContext context) {
    return ExtensionContextHelper.getAnnotatedFieldValue(
        context,
        DataSourceScriptDir.class,
        (field, extensionContext) -> {
          try {
            DataSourceScriptDir annotation = field.getAnnotation(DataSourceScriptDir.class);
            return ((Path) field.get(extensionContext.getRequiredTestClass()))
                .resolve(annotation.fileName());
          } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
          }
        });
  }
}
