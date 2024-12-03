package it.moneyverse.test.utils;

import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;

import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

public abstract class AbstractIntegrationTest {

  protected static TestContext testContext;

  @Value("${spring.security.base-path}")
  protected String basePath;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired protected TestRestTemplate restTemplate;

  protected AbstractIntegrationTest() {}
}
