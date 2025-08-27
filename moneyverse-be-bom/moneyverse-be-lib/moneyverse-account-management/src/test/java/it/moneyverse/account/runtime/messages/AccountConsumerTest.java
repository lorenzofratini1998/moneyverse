package it.moneyverse.account.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import it.moneyverse.account.model.AccountTestContext;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.UserModel;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
      UserDeletionTopic.TOPIC,
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
class AccountConsumerTest {

  protected static AccountTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  private BigDecimal exchangeRate;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private AccountRepository accountRepository;
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
    testContext = new AccountTestContext().generateScript(tempDir);
  }

  @BeforeEach
  void setup() {
    doNothing().when(eventService).publishEvent(any(UUID.class), anyString(), anyString());
    exchangeRate = RandomUtils.flipCoin() ? BigDecimal.ONE : RandomUtils.randomBigDecimal();
    mockServer.mockExchangeRate(exchangeRate);
  }

  @Test
  void testOnUserDeletion_Success() {
    final UUID userId = testContext.getRandomUser().getUserId();
    String event = TestFactory.fakeUserEvent(userId).toString();
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockNonExistentUser();
    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> assertTrue(accountRepository.findAccountByUserId(userId).isEmpty()));
  }

  @Test
  void testOnTransactionCreation() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts = testContext.getUserAccounts(userModel.getUserId());
    Account account = userAccounts.get(RandomUtils.randomInteger(userAccounts.size() - 1));
    TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder().withAccountId(account.getAccountId()).build();
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionCreationTopic.TOPIC, RandomUtils.randomUUID(), event.value());

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    account
                        .getBalance()
                        .add(
                            event
                                .getAmount()
                                .multiply(exchangeRate)
                                .setScale(2, RoundingMode.HALF_UP)),
                    accountRepository
                        .findById(account.getAccountId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getBalance()));
  }

  @Test
  void testOnTransactionDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts = testContext.getUserAccounts(userModel.getUserId());
    Account account = userAccounts.get(RandomUtils.randomInteger(userAccounts.size() - 1));
    TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder().withAccountId(account.getAccountId()).build();
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionDeletionTopic.TOPIC, RandomUtils.randomUUID(), event.value());

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    account
                        .getBalance()
                        .subtract(
                            event
                                .getAmount()
                                .multiply(exchangeRate)
                                .setScale(2, RoundingMode.HALF_UP)),
                    accountRepository
                        .findById(account.getAccountId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getBalance()));
  }

  @Test
  void testOnTransactionUpdate() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts =
        new ArrayList<>(testContext.getUserAccounts(userModel.getUserId()));
    Account previousAccount = userAccounts.get(RandomUtils.randomInteger(userAccounts.size() - 1));
    userAccounts.remove(previousAccount);
    Account account = userAccounts.get(RandomUtils.randomInteger(userAccounts.size() - 1));
    TransactionEvent event =
        TestFactory.TransactionEventBuilder.builder()
            .withPreviousTransaction(
                TestFactory.TransactionEventBuilder.builder()
                    .withAccountId(previousAccount.getAccountId())
                    .build())
            .withAccountId(account.getAccountId())
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
              Account prevAccount =
                  accountRepository
                      .findById(previousAccount.getAccountId())
                      .orElseThrow(IllegalArgumentException::new);
              Account acc =
                  accountRepository
                      .findById(account.getAccountId())
                      .orElseThrow(IllegalArgumentException::new);

              BigDecimal expectedPreviousAccountBalance =
                  previousAccount
                      .getBalance()
                      .subtract(event.getPreviousTransaction().getAmount().multiply(exchangeRate));
              BigDecimal expectedAccountBalance =
                  account.getBalance().add(event.getAmount().multiply(exchangeRate));

              assertEquals(
                  expectedPreviousAccountBalance.setScale(2, RoundingMode.HALF_UP),
                  prevAccount.getBalance());
              assertEquals(
                  expectedAccountBalance.setScale(2, RoundingMode.HALF_UP), acc.getBalance());
            });
  }
}
