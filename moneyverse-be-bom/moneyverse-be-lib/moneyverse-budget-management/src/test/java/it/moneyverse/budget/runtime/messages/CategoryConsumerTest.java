package it.moneyverse.budget.runtime.messages;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import it.moneyverse.budget.model.BudgetTestContext;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
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
    topics = {UserDeletionTopic.TOPIC})
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.RedisAutoConfiguration",
      "spring.kafka.admin.bootstrap-servers=${spring.embedded.kafka.brokers}",
      "grpc.server.budget-service.port=0"
    })
@ExtendWith(MockitoExtension.class)
class CategoryConsumerTest {

  protected static BudgetTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Autowired private KafkaTemplate<UUID, String> kafkaTemplate;
  @Autowired private CategoryRepository categoryRepository;
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
        .withEmbeddedKafka()
        .withFlywayTestDirectory(tempDir);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new BudgetTestContext().generateScript(tempDir);
  }

  @Test
  void testOnUserDeletion() {
    final UserModel userModel = testContext.getRandomUser();
    UserEvent event = TestFactory.fakeUserEvent(userModel.getUserId());
    final ProducerRecord<UUID, String> producerRecord =
        new ProducerRecord<>(UserDeletionTopic.TOPIC, event.key(), event.value());

    doNothing().when(eventService).publishEvent(any(UUID.class), anyString(), anyString());
    mockServer.mockNonExistentUser();

    kafkaTemplate.send(producerRecord);

    await()
        .pollDelay(5, TimeUnit.SECONDS)
        .pollInterval(5, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertTrue(
                  categoryRepository.findCategoriesByUserId(userModel.getUserId()).isEmpty());
              assertTrue(
                  budgetRepository.findAll().stream()
                      .filter(
                          budget -> budget.getCategory().getUserId().equals(userModel.getUserId()))
                      .toList()
                      .isEmpty());
            });
  }
}
