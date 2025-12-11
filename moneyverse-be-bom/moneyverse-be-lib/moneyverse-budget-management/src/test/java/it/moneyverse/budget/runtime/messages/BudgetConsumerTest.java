package it.moneyverse.budget.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import it.moneyverse.budget.model.BudgetTestContext;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
@EmbeddedKafka(
    partitions = 1,
    topics = {
      TransactionCreationTopic.TOPIC,
      TransactionDeletionTopic.TOPIC,
      TransactionUpdateTopic.TOPIC
    })
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.RedisAutoConfiguration",
      "spring.kafka.admin.bootstrap-servers=${spring.embedded.kafka.brokers}"
    })
@ExtendWith(MockitoExtension.class)
class BudgetConsumerTest {

  private static final BigDecimal TOLERANCE = BigDecimal.valueOf(0.01);

  protected static BudgetTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  private BigDecimal exchangeRate;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private BudgetRepository budgetRepository;
  @MockitoBean private SecurityService securityService;
  @MockitoBean private SseEventService eventService;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withFlywayTestDirectory(tempDir)
        .withEmbeddedKafka();
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new BudgetTestContext().generateScript(tempDir);
  }

  @BeforeEach
  void setup() {
    doNothing().when(eventService).publishEvent(any(UUID.class), anyString(), anyString());
    exchangeRate = RandomUtils.flipCoin() ? BigDecimal.ONE : RandomUtils.randomBigDecimal();
    mockServer.mockExchangeRate(exchangeRate);
  }

  @Test
  void testOnTransactionCreation() {
    final Budget budget =
        testContext
            .getBudgets()
            .get(RandomUtils.randomInteger(0, testContext.getBudgets().size() - 1));
    TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder().withBudgetId(budget.getBudgetId()).build();
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(TransactionCreationTopic.TOPIC, event.key(), event.value());

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                checkExpectedWithActualBudgetAmount(
                    budget
                        .getAmount()
                        .add(event.getAmount().multiply(exchangeRate))
                        .setScale(2, RoundingMode.HALF_UP),
                    budgetRepository
                        .findById(budget.getBudgetId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getAmount()));
  }

  @Test
  void testOnTransactionDeletion() {
    final Budget budget =
        testContext
            .getBudgets()
            .get(RandomUtils.randomInteger(0, testContext.getBudgets().size() - 1));
    TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder().withBudgetId(budget.getBudgetId()).build();
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionDeletionTopic.TOPIC, event.getTransactionId(), event.value());

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              checkExpectedWithActualBudgetAmount(
                  budget
                      .getAmount()
                      .subtract(event.getAmount().multiply(exchangeRate))
                      .setScale(2, RoundingMode.HALF_UP),
                  budgetRepository
                      .findById(budget.getBudgetId())
                      .orElseThrow(IllegalArgumentException::new)
                      .getAmount());
            });
  }

  @Test
  void testOnTransactionUpdate() {
    final Budget budget =
        testContext
            .getBudgets()
            .get(RandomUtils.randomInteger(0, testContext.getBudgets().size() - 1));
    final List<Budget> userBudgets =
        testContext.getBudgets().stream()
            .filter(b -> b.getCategory().getUserId().equals(budget.getCategory().getUserId()))
            .collect(Collectors.toList());
    userBudgets.remove(budget);
    Budget previousBudget = userBudgets.get(RandomUtils.randomInteger(userBudgets.size() - 1));
    final TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder()
            .withBudgetId(budget.getBudgetId())
            .withPreviousTransaction(
                TestFactory.TransactionEventBuilder.builder()
                    .withBudgetId(previousBudget.getBudgetId())
                    .build())
            .build();

    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(TransactionUpdateTopic.TOPIC, event.key(), event.value());

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Budget prevBudget =
                  budgetRepository
                      .findById(previousBudget.getBudgetId())
                      .orElseThrow(IllegalArgumentException::new);
              Budget b =
                  budgetRepository
                      .findById(budget.getBudgetId())
                      .orElseThrow(IllegalArgumentException::new);

              BigDecimal expectedPreviousBudgetAmount =
                  previousBudget
                      .getAmount()
                      .subtract(event.getPreviousTransaction().getAmount().multiply(exchangeRate));
              BigDecimal expectedBudgetAmount =
                  budget.getAmount().add(event.getAmount().multiply(exchangeRate));

              checkExpectedWithActualBudgetAmount(
                  expectedPreviousBudgetAmount.setScale(2, RoundingMode.HALF_UP),
                  prevBudget.getAmount());

              checkExpectedWithActualBudgetAmount(
                  expectedBudgetAmount.setScale(2, RoundingMode.HALF_UP), b.getAmount());
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
