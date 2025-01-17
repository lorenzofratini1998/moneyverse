package it.moneyverse.transaction.runtime.controllers;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionTestContext;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
public class TransactionManagementControllerIT extends AbstractIntegrationTest {

  protected static TransactionTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();

  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private TransactionRepository transactionRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcAccountService(mockServer.getHost(), mockServer.getPort())
        .withGrpcBudgetService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        new TransactionTestContext()
            .generateScript(tempDir)
            .insertUsersIntoKeycloak(keycloakContainer);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateTransaction() {
    final String username = testContext.getRandomUser().getUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final TransactionRequestDto request = testContext.createTransactionRequest(username);
    mockServer.mockExistentAccount();
    mockServer.mockExistentBudget();
    TransactionDto expected = testContext.getExpectedTransactionDto(request);

    ResponseEntity<TransactionDto> response =
        restTemplate.postForEntity(
            basePath + "/transactions", new HttpEntity<>(request, headers), TransactionDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() + 1, transactionRepository.findAll().size());
    assertNotNull(response.getBody());
    assertEquals(expected.getUsername(), response.getBody().getUsername());
    assertEquals(expected.getAccountId(), response.getBody().getAccountId());
    assertEquals(expected.getBudgetId(), response.getBody().getBudgetId());
    assertEquals(expected.getDate(), response.getBody().getDate());
    assertEquals(expected.getDescription(), response.getBody().getDescription());
    assertEquals(expected.getAmount(), response.getBody().getAmount());
    assertEquals(expected.getCurrency(), response.getBody().getCurrency());
    assertEquals(Collections.emptySet(), response.getBody().getTags());
  }
}
