package it.moneyverse.test.annotations.datasource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

public class PopulateDatabaseExtension implements BeforeEachCallback {

  private static final String SQL_SEPARATOR = ";";

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    PopulateDatabase classAnnotation = context.getRequiredTestClass()
        .getAnnotation(PopulateDatabase.class);
    PopulateDatabase methodAnnotation = context.getRequiredTestMethod()
        .getAnnotation(PopulateDatabase.class);

    if (classAnnotation != null && methodAnnotation != null) {
      throw new IllegalStateException("You cannot use %s both on class and methods".formatted(
          PopulateDatabase.class.getName()));
    }

    if (methodAnnotation != null) {
      executeScript(context, methodAnnotation.script());
    } else if (classAnnotation != null) {
      executeScript(context, classAnnotation.script());
    }
  }

  private void executeScript(ExtensionContext context, String script) throws IOException {
    JdbcTemplate jdbcTemplate = SpringExtension.getApplicationContext(context)
        .getBean(JdbcTemplate.class);
    String[] sqlStatements = FileCopyUtils.copyToString(
        new InputStreamReader(new ClassPathResource(script).getInputStream(),
            StandardCharsets.UTF_8)).split(SQL_SEPARATOR);
    jdbcTemplate.batchUpdate(sqlStatements);
  }

}
