package it.moneyverse.transaction.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.AccountDeletionEvent;
import it.moneyverse.core.model.events.BudgetDeletionEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.TransactionTestContext;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
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
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration",
      "logging.level.org.grpcmock.GrpcMock=WARN"
    })
@Testcontainers
@CleanDatabaseAfterEachTest
class TransactionConsumerTest {

  private static TransactionTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private TransactionRepository transactionRepository;

  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKafkaContainer(kafkaContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcAccountService(mockServer.getHost(), mockServer.getPort())
        .withGrpcBudgetService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort());
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new TransactionTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion() {
    final String username = testContext.getRandomUser().getUsername();
    final List<Transaction> userBudgets = testContext.getTransactions(username);
    final long initialSize = transactionRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(username));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(initialSize - userBudgets.size(), transactionRepository.count()));
  }

  @Test
  void testOnAccountDeletion() {
    final UUID accountId =
        testContext
            .getTransactions()
            .get(RandomUtils.randomInteger(0, testContext.getTransactions().size() - 1))
            .getAccountId();
    final List<Transaction> accountTransactions = testContext.getTransactionsByAccountId(accountId);
    final long initialSize = transactionRepository.count();
    String event = JsonUtils.toJson(new AccountDeletionEvent(accountId));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(AccountDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockExistentAccount();

    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    initialSize - accountTransactions.size(), transactionRepository.count()));
  }

  @Test
  void testOnBudgetDeletion() {
    final UUID budgetId =
        testContext
            .getTransactions()
            .get(RandomUtils.randomInteger(0, testContext.getTransactions().size() - 1))
            .getBudgetId();
    final List<Transaction> budgetTransactions = testContext.getTransactionsByAccountId(budgetId);
    String event = JsonUtils.toJson(new BudgetDeletionEvent(budgetId));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(BudgetDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockExistentBudget();

    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              for (Transaction transaction : budgetTransactions) {
                assertNull(
                    transactionRepository
                        .findById(transaction.getTransactionId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getBudgetId());
              }
            });
  }
}
