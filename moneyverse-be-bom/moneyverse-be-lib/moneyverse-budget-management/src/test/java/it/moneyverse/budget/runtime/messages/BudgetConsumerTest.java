package it.moneyverse.budget.runtime.messages;

import static it.moneyverse.test.utils.FakeUtils.randomTransactionEvent;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.RedisAutoConfiguration",
      "logging.level.org.grpcmock.GrpcMock=WARN",
      "logging.level.org.apache.kafka.clients=ERROR",
      "logging.level.org.springframework.kafka.listener=ERROR"
    })
@Testcontainers
@CleanDatabaseAfterEachTest
class BudgetConsumerTest {

  private static final BigDecimal TOLERANCE = new BigDecimal("0.01");

  protected static BudgetTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BudgetRepository budgetRepository;

  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKafkaContainer(kafkaContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort());
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new BudgetTestContext().generateScript(tempDir);
  }

  @Test
  void testOnTransactionCreation() {
    final UserModel userModel = testContext.getRandomUser();
    final Budget budget = testContext.getRandomBudgetByUserId(userModel.getUserId());
    final TransactionEvent event = randomTransactionEvent(budget.getBudgetId());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionCreationTopic.TOPIC, event.getTransactionId(), JsonUtils.toJson(event));

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              BigDecimal expectedAmount =
                  budget.getAmount().add(event.getAmount()).setScale(2, RoundingMode.HALF_UP);
              BigDecimal actualAmount =
                  Objects.requireNonNull(
                          budgetRepository.findById(budget.getBudgetId()).orElse(null))
                      .getAmount()
                      .setScale(2, RoundingMode.HALF_UP);
              checkExpectedWithActualBudgetAmount(expectedAmount, actualAmount);
            });
  }

  @Test
  void testOnTransactionDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    final Budget budget = testContext.getRandomBudgetByUserId(userModel.getUserId());
    final TransactionEvent event = randomTransactionEvent(budget.getBudgetId());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionDeletionTopic.TOPIC, event.getTransactionId(), JsonUtils.toJson(event));

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              BigDecimal expectedAmount =
                  budget.getAmount().subtract(event.getAmount()).setScale(2, RoundingMode.HALF_UP);
              BigDecimal actualAmount =
                  Objects.requireNonNull(
                          budgetRepository.findById(budget.getBudgetId()).orElse(null))
                      .getAmount()
                      .setScale(2, RoundingMode.HALF_UP);
              checkExpectedWithActualBudgetAmount(expectedAmount, actualAmount);
            });
  }

  @Test
  void testOnTransactionUpdate() {
    final UserModel userModel = testContext.getRandomUser();
    final Budget budget = testContext.getRandomBudgetByUserId(userModel.getUserId());
    final TransactionEvent event = randomTransactionEvent(budget.getBudgetId());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionUpdateTopic.TOPIC, event.getTransactionId(), JsonUtils.toJson(event));

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              BigDecimal expectedAmount =
                  budget
                      .getAmount()
                      .add(event.getAmount().subtract(event.getPreviousAmount()))
                      .setScale(2, RoundingMode.HALF_UP);
              BigDecimal actualAmount =
                  Objects.requireNonNull(
                          budgetRepository.findById(budget.getBudgetId()).orElse(null))
                      .getAmount()
                      .setScale(2, RoundingMode.HALF_UP);
              checkExpectedWithActualBudgetAmount(expectedAmount, actualAmount);
            });
  }

  private void checkExpectedWithActualBudgetAmount(
      BigDecimal expectedAmount, BigDecimal actualAmount) {
    BigDecimal difference = expectedAmount.subtract(actualAmount).abs();

    assertTrue(
        difference.compareTo(TOLERANCE) <= 0,
        String.format(
            "Difference (%s) exceeds tolerance. Expected: %s, Actual: %s",
            difference, expectedAmount, actualAmount));
  }
}
