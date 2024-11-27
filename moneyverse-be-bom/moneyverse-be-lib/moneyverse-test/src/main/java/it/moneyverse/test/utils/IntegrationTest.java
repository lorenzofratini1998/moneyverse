package it.moneyverse.test.utils;

import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;

import java.nio.file.Path;

import it.moneyverse.test.model.RandomTestContextCreator;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.utils.helper.ScriptHelper;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CleanDatabaseAfterEachTest
public abstract class IntegrationTest {

  protected TestContextModel testContext;

  @DataSourceScriptDir(fileName = ScriptHelper.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  protected IntegrationTest() {
    testContext = new TestContext(new RandomTestContextCreator());
  }

}
