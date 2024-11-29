package it.moneyverse.test.utils;

import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;

import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import java.nio.file.Path;

public abstract class IntegrationTest {

  protected static TestContextModel testModel;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  protected IntegrationTest() {
  }

}
