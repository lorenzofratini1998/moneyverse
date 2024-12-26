package it.moneyverse.budget.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.DefaultBudgetTemplateRepository;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.model.beans.UserCreationTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.model.events.UserCreationEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.test.annotations.datasource.CleanDatabaseAfterEachTest;
import it.moneyverse.test.annotations.datasource.DataSourceScriptDir;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
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
import org.springframework.kafka.core.KafkaOperations;
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
public class BudgetConsumerTest {

  protected static BudgetTestContext testContext;

  @DataSourceScriptDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private BudgetRepository budgetRepository;
  @Autowired private DefaultBudgetTemplateRepository defaultBudgetTemplateRepository;

  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @RegisterExtension static GrpcMockUserService mockUserService = new GrpcMockUserService();

  @Autowired private KafkaOperations<UUID, String> operations;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKafkaContainer(kafkaContainer)
        .withGrpcUserService(mockUserService.getHost(), mockUserService.getPort());
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new BudgetTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Budget> userBudgets = testContext.getBudgets(userModel.getUsername());
    final long initialSize = budgetRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(userModel.getUsername()));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockUserService.mockExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(initialSize - userBudgets.size(), budgetRepository.count()));
  }

  @Test
  void testOnUserCreation() {
    final long initialSize = budgetRepository.count();
    String event =
        JsonUtils.toJson(
            new UserCreationEvent(
                RandomUtils.randomString(15), RandomUtils.randomEnum(CurrencyEnum.class)));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserCreationTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockUserService.mockExistentUser();
    kafkaTemplate.send(producerRecord);

    await()
        .pollInterval(Duration.ofSeconds(5))
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    initialSize + defaultBudgetTemplateRepository.findAll().size(),
                    budgetRepository.count()));
  }
}
