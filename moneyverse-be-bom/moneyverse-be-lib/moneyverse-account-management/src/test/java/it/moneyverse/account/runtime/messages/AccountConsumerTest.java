package it.moneyverse.account.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.model.events.TransactionEvent;
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
    testContext = new AccountTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion_Success() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts = testContext.getUserAccounts(userModel.getUserId());
    final long initialSize = accountRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(userModel.getUserId()));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockNonExistentUser();
    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(initialSize - userAccounts.size(), accountRepository.count()));
  }

  @Test
  void testOnTransactionCreation() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts = testContext.getUserAccounts(userModel.getUserId());
    Account account = userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    TransactionEvent event = new TransactionEvent();
    event.setAccountId(account.getAccountId());
    event.setAmount(RandomUtils.randomBigDecimal());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionCreationTopic.TOPIC, RandomUtils.randomUUID(), JsonUtils.toJson(event));

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    account.getBalance().add(event.getAmount().setScale(2, RoundingMode.HALF_UP)),
                    accountRepository
                        .findById(account.getAccountId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getBalance()));
  }

  @Test
  void testOnTransactionDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts = testContext.getUserAccounts(userModel.getUserId());
    Account account = userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    TransactionEvent event = new TransactionEvent();
    event.setAccountId(account.getAccountId());
    event.setAmount(RandomUtils.randomBigDecimal());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionDeletionTopic.TOPIC, RandomUtils.randomUUID(), JsonUtils.toJson(event));

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
                        .subtract(event.getAmount().setScale(2, RoundingMode.HALF_UP)),
                    accountRepository
                        .findById(account.getAccountId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getBalance()));
  }

  @Test
  void testOnTransactionUpdate_AccountChanged() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts =
        new ArrayList<>(testContext.getUserAccounts(userModel.getUserId()));
    Account previousAccount =
        userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    userAccounts.remove(previousAccount);
    Account account = userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    TransactionEvent event = new TransactionEvent();
    event.setPreviousAccountId(previousAccount.getAccountId());
    event.setAccountId(account.getAccountId());
    event.setAmount(RandomUtils.randomBigDecimal());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionUpdateTopic.TOPIC, RandomUtils.randomUUID(), JsonUtils.toJson(event));

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
                  previousAccount.getBalance().add(event.getAmount().negate());
              BigDecimal expectedAccountBalance = account.getBalance().add(event.getAmount());

              assertEquals(
                  expectedPreviousAccountBalance.setScale(2, RoundingMode.HALF_UP),
                  prevAccount.getBalance());
              assertEquals(
                  expectedAccountBalance.setScale(2, RoundingMode.HALF_UP), acc.getBalance());
            });
  }

  @Test
  void testOnTransactionUpdate_AccountChangedAndAmountChanged() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts =
        new ArrayList<>(testContext.getUserAccounts(userModel.getUserId()));
    Account previousAccount =
        userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    userAccounts.remove(previousAccount);
    Account account = userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    TransactionEvent event = new TransactionEvent();
    event.setPreviousAccountId(previousAccount.getAccountId());
    event.setAccountId(account.getAccountId());
    event.setAmount(RandomUtils.randomBigDecimal());
    event.setPreviousAmount(RandomUtils.randomBigDecimal());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionUpdateTopic.TOPIC, RandomUtils.randomUUID(), JsonUtils.toJson(event));

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
                  previousAccount.getBalance().add(event.getPreviousAmount().negate());
              BigDecimal expectedAccountBalance = account.getBalance().add(event.getAmount());

              assertEquals(
                  expectedPreviousAccountBalance.setScale(2, RoundingMode.HALF_UP),
                  prevAccount.getBalance());
              assertEquals(
                  expectedAccountBalance.setScale(2, RoundingMode.HALF_UP), acc.getBalance());
            });
  }

  @Test
  void testOnTransactionUpdate_AmountChanged() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Account> userAccounts =
        new ArrayList<>(testContext.getUserAccounts(userModel.getUserId()));
    Account account = userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    TransactionEvent event = new TransactionEvent();
    event.setAccountId(account.getAccountId());
    event.setAmount(RandomUtils.randomBigDecimal());
    event.setPreviousAmount(RandomUtils.randomBigDecimal());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(
            TransactionUpdateTopic.TOPIC, RandomUtils.randomUUID(), JsonUtils.toJson(event));

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Account acc =
                  accountRepository
                      .findById(account.getAccountId())
                      .orElseThrow(IllegalArgumentException::new);

              BigDecimal expectedAccountBalance =
                  account.getBalance().add(event.getAmount().subtract(event.getPreviousAmount()));

              assertEquals(
                  expectedAccountBalance.setScale(2, RoundingMode.HALF_UP), acc.getBalance());
            });
  }
}
