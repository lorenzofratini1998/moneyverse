package it.moneyverse.test.utils;

import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

public abstract class AbstractIntegrationTest {

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Value("${spring.security.base-path}")
  protected String basePath;

  @Autowired protected TestRestTemplate restTemplate;

  protected AbstractIntegrationTest() {}

  protected static BigDecimal round(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
