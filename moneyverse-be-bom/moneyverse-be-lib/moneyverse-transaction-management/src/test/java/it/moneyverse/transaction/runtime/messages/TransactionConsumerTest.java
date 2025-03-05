package it.moneyverse.transaction.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.TransactionTestContext;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
@EmbeddedKafka(
    partitions = 1,
    topics = {UserDeletionTopic.TOPIC, AccountDeletionTopic.TOPIC, CategoryDeletionTopic.TOPIC})
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.RedisAutoConfiguration",
      "spring.kafka.admin.bootstrap-servers=${spring.embedded.kafka.brokers}"
    })
class TransactionConsumerTest {

  private static TransactionTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private TagRepository tagRepository;
  @Autowired private TransferRepository transferRepository;
  @Autowired private SubscriptionRepository subscriptionRepository;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcAccountService(mockServer.getHost(), mockServer.getPort())
        .withGrpcBudgetService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withFlywayTestDirectory(tempDir)
        .withEmbeddedKafka();
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new TransactionTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion() {
    final UUID userId = testContext.getRandomUser().getUserId();
    UserEvent event = TestFactory.fakeUserEvent(userId);
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, event.key(), event.value());

    mockServer.mockNonExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertTrue(transactionRepository.findTransactionByUserId(userId).isEmpty());
              assertTrue(tagRepository.findByUserId(userId).isEmpty());
              assertTrue(transferRepository.findTransferByUserId(userId).isEmpty());
              assertTrue(subscriptionRepository.findSubscriptionByUserId(userId).isEmpty());
            });
  }

  @Test
  void testOnAccountDeletion() {
    final UUID accountId =
        testContext
            .getTransactions()
            .get(RandomUtils.randomInteger(testContext.getTransactions().size()))
            .getAccountId();
    AccountEvent event = TestFactory.fakeAccountEvent(accountId);
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(AccountDeletionTopic.TOPIC, event.key(), event.value());

    mockServer.mockNonExistentAccount();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertTrue(transactionRepository.findTransactionByAccountId(accountId).isEmpty());
              assertTrue(transferRepository.findTransferByAccountId(accountId).isEmpty());
              assertTrue(subscriptionRepository.findSubscriptionByAccountId(accountId).isEmpty());
            });
  }

  @Test
  void testOnCategoryDeletion() {
    final UUID categoryId =
        testContext
            .getTransactions()
            .get(RandomUtils.randomInteger(testContext.getTransactions().size()))
            .getCategoryId();
    final List<Transaction> categoryTransactions =
        testContext.getTransactionsByCategoryId(categoryId);
    CategoryEvent event = TestFactory.fakeCategoryEvent(categoryId);
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(CategoryDeletionTopic.TOPIC, event.key(), event.value());

    mockServer.mockNonExistentCategory();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              for (Transaction transaction : categoryTransactions) {
                assertNull(
                    transactionRepository
                        .findById(transaction.getTransactionId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getCategoryId());
              }
            });
  }
}
