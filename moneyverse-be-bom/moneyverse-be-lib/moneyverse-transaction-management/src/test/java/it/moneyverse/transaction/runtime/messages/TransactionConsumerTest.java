package it.moneyverse.transaction.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.AccountDeletionEvent;
import it.moneyverse.core.model.events.CategoryDeletionEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.core.utils.properties.KafkaProperties;
import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.TransactionTestContext;
import java.nio.file.Path;
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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.RedisAutoConfiguration",
      "logging.level.org.grpcmock.GrpcMock=WARN",
      "spring.kafka.admin.bootstrap-servers=${spring.embedded.kafka.brokers}"
    })
@Testcontainers
@CleanDatabaseAfterEachTest
@EmbeddedKafka(
    partitions = 1,
    topics = {UserDeletionTopic.TOPIC, AccountDeletionTopic.TOPIC, CategoryDeletionTopic.TOPIC})
class TransactionConsumerTest {

  private static TransactionTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private TagRepository tagRepository;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcAccountService(mockServer.getHost(), mockServer.getPort())
        .withGrpcBudgetService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort());
    registry.add(
        KafkaProperties.KafkaConsumerProperties.GROUP_ID,
        () -> RandomUtils.randomUUID().toString());
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new TransactionTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final List<Transaction> userTransactions = testContext.getTransactions(userId);
    final List<Tag> userTags = testContext.getUserTags(userId);
    final long initialSize = transactionRepository.count();
    final long initialTagSize = tagRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(userId));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockNonExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertEquals(initialSize - userTransactions.size(), transactionRepository.count());
              assertEquals(initialTagSize - userTags.size(), tagRepository.count());
            });
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

    mockServer.mockNonExistentAccount();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    initialSize - accountTransactions.size(), transactionRepository.count()));
  }

  @Test
  void testOnCategoryDeletion() {
    final UUID categoryId =
        testContext
            .getTransactions()
            .get(RandomUtils.randomInteger(0, testContext.getTransactions().size() - 1))
            .getCategoryId();
    final List<Transaction> categoryTransactions =
        testContext.getTransactionsByCategoryId(categoryId);
    String event = JsonUtils.toJson(new CategoryDeletionEvent(categoryId));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(CategoryDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockNonExistentCategory();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(60, TimeUnit.SECONDS)
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
