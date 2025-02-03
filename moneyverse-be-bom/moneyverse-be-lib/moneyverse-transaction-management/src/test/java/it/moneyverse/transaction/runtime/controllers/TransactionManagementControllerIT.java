package it.moneyverse.transaction.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.TransactionTestContext;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class TransactionManagementControllerIT extends AbstractIntegrationTest {

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
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new TransactionTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateTransaction() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final TransactionRequestDto request = testContext.createTransactionRequest(userId);
    mockServer.mockExistentAccount();
    mockServer.mockExistentBudget();
    mockServer.mockExistentCurrency();
    TransactionDto expected = testContext.getExpectedTransactionDto(request);

    ResponseEntity<TransactionDto> response =
        restTemplate.postForEntity(
            basePath + "/transactions", new HttpEntity<>(request, headers), TransactionDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() + 1, transactionRepository.findAll().size());
    assertNotNull(response.getBody());
    assertEquals(expected.getUserId(), response.getBody().getUserId());
    assertEquals(expected.getAccountId(), response.getBody().getAccountId());
    assertEquals(expected.getBudgetId(), response.getBody().getBudgetId());
    assertEquals(expected.getDate(), response.getBody().getDate());
    assertEquals(expected.getDescription(), response.getBody().getDescription());
    assertEquals(expected.getAmount(), response.getBody().getAmount());
    assertEquals(expected.getCurrency(), response.getBody().getCurrency());
    assertEquals(Collections.emptySet(), response.getBody().getTags());
  }

  @Test
  void testGetTransactions() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final TransactionCriteria criteria = testContext.createTransactionCriteria();
    criteria.setUserId(userId);
    final List<Transaction> expected = testContext.filterTransactions(criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    ResponseEntity<List<TransactionDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/transactions", criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  void testGetTransaction() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID transactionId = testContext.getRandomTransaction(userId).getTransactionId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<TransactionDto> response =
        restTemplate.exchange(
            basePath + "/transactions/" + transactionId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            TransactionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transactionId, response.getBody().getTransactionId());
  }

  @Test
  void testUpdateTransaction() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID transactionId = testContext.getRandomTransaction(userId).getTransactionId();
    TransactionUpdateRequestDto request =
        new TransactionUpdateRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2024),
            RandomUtils.randomString(30),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            Collections.singleton(testContext.getRandomTag(userId).getTagId()));
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency();

    ResponseEntity<TransactionDto> response =
        restTemplate.exchange(
            basePath + "/transactions/" + transactionId,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            TransactionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transactionId, response.getBody().getTransactionId());
    assertEquals(request.accountId(), response.getBody().getAccountId());
    assertEquals(request.budgetId(), response.getBody().getBudgetId());
    assertEquals(request.date(), response.getBody().getDate());
    assertEquals(request.description(), response.getBody().getDescription());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(request.tags().size(), response.getBody().getTags().size());
  }

  @Test
  void testDeleteTransaction() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID transactionId = testContext.getRandomTransaction(userId).getTransactionId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/transactions/" + transactionId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() - 1, transactionRepository.findAll().size());
  }
}
