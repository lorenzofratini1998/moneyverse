package it.moneyverse.test.model;

import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import jakarta.persistence.EntityManager;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CleanDatabaseAfterEachTest
public abstract class IntegrationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTest.class);

  protected TestContext testContext;

  @DataSourceScriptDir
  protected static Path tempDir;

  protected IntegrationTest() {
    testContext = new TestContext(new RandomTestContextCreator());
  }

}
