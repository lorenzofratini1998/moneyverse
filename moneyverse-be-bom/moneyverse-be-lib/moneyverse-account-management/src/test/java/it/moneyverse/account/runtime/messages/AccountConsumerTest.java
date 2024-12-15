package it.moneyverse.account.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.event.UserDeletionEvent;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;
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
class AccountConsumerTest {

  protected static AccountTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private AccountRepository accountRepository;
  @Autowired private ConsumerFactory<UUID, String> consumerFactory;

  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockUserService mockUserService = new GrpcMockUserService();

  private KafkaMessageListenerContainer<UUID, String> container;
  private BlockingQueue<ConsumerRecord<UUID, String>> consumerRecords;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKafkaContainer(kafkaContainer)
        .withGrpcUserService(mockUserService.getHost(), mockUserService.getPort());
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        AccountTestContext.builder()
            .withStrategy(TestModelStrategyEnum.RANDOM)
            .withTestUsers()
            .withTestAccount()
            .withScriptMetadata(new ScriptMetadata(tempDir, Account.class))
            .build();
  }

  @BeforeEach
  void setup() {
    consumerRecords = new LinkedBlockingQueue<>();
    ContainerProperties containerProperties = new ContainerProperties(UserDeletionTopic.TOPIC + "-dlt");
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    container.setupMessageListener(
        (MessageListener<UUID, String>) record -> consumerRecords.add(record));
    container.start();

    ContainerTestUtils.waitForAssignment(container, 0);
  }

  @AfterEach
  void tearDown() {
    container.stop();
  }

  @Test
  void testOnMessage_Success() {
    final UserModel userModel = testContext.getRandomUser();
    final List<AccountModel> userAccounts = testContext.getUserAccounts(userModel.getUsername());
    final long initialSize = accountRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(userModel.getUsername()));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockUserService.mockExistentUser();
    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(initialSize - userAccounts.size(), accountRepository.count()));
  }

  @Test
  void testOnError() {
    final String nonExistentUsername = RandomUtils.randomString(25);
    String event = JsonUtils.toJson(new UserDeletionEvent(nonExistentUsername));

    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockUserService.mockNonExistentUser();
    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              ConsumerRecord<UUID, String> dltRecord = consumerRecords.poll(5, TimeUnit.SECONDS);
              assertNotNull(dltRecord, "Expected a message in the Dead Letter Topic, but none was found.");
              assertEquals(event, dltRecord.value(), "The message value in DLT does not match the original event.");
            });
  }
}
