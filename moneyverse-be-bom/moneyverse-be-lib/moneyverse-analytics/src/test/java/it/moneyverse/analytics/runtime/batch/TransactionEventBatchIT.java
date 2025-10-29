package it.moneyverse.analytics.runtime.batch;

import it.moneyverse.analytics.model.AnalyticsTestContext;
import it.moneyverse.analytics.model.repositories.TransactionEventBufferRepository;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.extensions.testcontainers.ClickhouseContainer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;

import java.nio.file.Path;

@MoneyverseTest
@SpringBatchTest
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration",
      "spring.batch.jdbc.initialize-schema=never"
    })
public class TransactionEventBatchIT {
  protected static AnalyticsTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static ClickhouseContainer clickhouseContainer = new ClickhouseContainer();

  @MockitoBean TransactionEventScheduler transactionEventScheduler;
  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired private TransactionEventBufferRepository transactionEventBufferRepository;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKafkaContainer(kafkaContainer)
        .withClickhouse(clickhouseContainer);
    registry.add(
        "spring.flyway.postgres.locations",
        () ->
            "classpath:db/migration/postgres/common,filesystem:%s"
                .formatted(tempDir.toAbsolutePath().toString()));
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new AnalyticsTestContext();
    testContext.generateScriptBatch(tempDir);
  }

  @Test
  void testTransactionEventJob() throws Exception {
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
    int initialSize = transactionEventBufferRepository.findAll().size();
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    Assertions.assertEquals(0, transactionEventBufferRepository.findAll().size());
  }
}
