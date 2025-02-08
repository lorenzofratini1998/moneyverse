package it.moneyverse.budget.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.UserModel;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration",
      "logging.level.org.grpcmock.GrpcMock=WARN",
      "logging.level.org.apache.kafka.clients.NetworkClient=ERROR"
    })
@Testcontainers
@CleanDatabaseAfterEachTest
class CategoryConsumerTest {

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
  void testOnUserDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    final List<Category> userCategories = testContext.getCategories(userModel.getUserId());
    final long initialSize = categoryRepository.count();
    String event = JsonUtils.toJson(new UserDeletionEvent(userModel.getUserId()));
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, RandomUtils.randomUUID(), event);

    mockServer.mockExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(15, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .atMost(90, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertEquals(initialSize - userCategories.size(), categoryRepository.count());
              List<Budget> userBudgets =
                  budgetRepository.findAll().stream()
                      .filter(
                          budget -> budget.getCategory().getUserId().equals(userModel.getUserId()))
                      .toList();
              assertEquals(0, userBudgets.size());
            });
  }
}
