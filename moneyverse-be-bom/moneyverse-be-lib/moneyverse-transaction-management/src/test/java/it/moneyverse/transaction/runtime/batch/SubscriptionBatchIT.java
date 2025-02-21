package it.moneyverse.transaction.runtime.batch;

import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.TransactionTestContext;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.RedisAutoConfiguration, it.moneyverse.core.boot.SecurityAutoConfiguration",
      "spring.batch.jdbc.initialize-schema=never"
    })
@SpringBatchTest
@Testcontainers
@CleanDatabaseAfterEachTest
class SubscriptionBatchIT {

  protected static TransactionTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();

  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;
  @Autowired private SubscriptionRepository subscriptionRepository;
  @Autowired private TransactionRepository transactionRepository;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withGrpcAccountService(mockServer.getHost(), mockServer.getPort())
        .withGrpcBudgetService(mockServer.getHost(), mockServer.getPort())
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new TransactionTestContext();
    testContext.getSubscriptions().stream()
        .filter(s -> s.getEndDate() != null)
        .forEach(s -> s.setNextExecutionDate(LocalDate.now()));
    testContext.generateScript(tempDir);
  }

  @Test
  void testSubscriptionJob() throws Exception {
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
    List<Subscription> subscriptions =
        subscriptionRepository.findSubscriptionByNextExecutionDateAndIsActive(
            LocalDate.now(), true);
    int initialSize = transactionRepository.findAll().size();

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    Assertions.assertEquals(
        initialSize + subscriptions.size(), transactionRepository.findAll().size());
  }
}
