package it.moneyverse.transaction.runtime.controllers;

import static it.moneyverse.transaction.utils.TransactionTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.extensions.testcontainers.RedisContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.TransactionTestContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
  @Container static RedisContainer redisContainer = new RedisContainer();

  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private TagRepository tagRepository;
  @Autowired private SubscriptionRepository subscriptionRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withRedis(redisContainer)
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
    mockServer.mockExistentCategory();
    mockServer.mockExistentCurrency(request.transactions().getFirst().currency());
    TransactionDto expected = testContext.getExpectedTransactionDto(request);

    ResponseEntity<List<TransactionDto>> response =
        restTemplate.exchange(
            basePath + "/transactions",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() + 1, transactionRepository.findAll().size());
    assertNotNull(response.getBody());
    TransactionDto actual = response.getBody().getFirst();
    assertEquals(expected.getUserId(), actual.getUserId());
    assertEquals(expected.getAccountId(), actual.getAccountId());
    assertEquals(expected.getCategoryId(), actual.getCategoryId());
    assertEquals(expected.getDate(), actual.getDate());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertEquals(expected.getAmount(), actual.getAmount());
    assertEquals(expected.getCurrency(), actual.getCurrency());
    assertEquals(Collections.emptySet(), actual.getTags());
  }

  @Test
  void testGetTransactions() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final TransactionCriteria criteria = testContext.createTransactionCriteria(userId);
    final List<Transaction> expected = testContext.filterTransactions(userId, criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    ResponseEntity<List<TransactionDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/transactions/users/" + userId, criteria),
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
            testContext.getRandomTag(userId));
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency(request.currency());

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
    assertEquals(request.categoryId(), response.getBody().getCategoryId());
    assertEquals(request.date(), response.getBody().getDate());
    assertEquals(request.description(), response.getBody().getDescription());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.currency(), response.getBody().getCurrency());
    if (request.tags() != null && !request.tags().isEmpty()) {
      assertEquals(request.tags().size(), response.getBody().getTags().size());
    }
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

  @Test
  void testCreateTransfer() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final TransferRequestDto request = testContext.createTransferRequest(userId);
    mockServer.mockExistentAccount();
    mockServer.mockExistentCurrency(request.currency());

    ResponseEntity<TransferDto> response =
        restTemplate.exchange(
            basePath + "/transfer",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() + 2, transactionRepository.findAll().size());
    assertNotNull(response.getBody());
  }

  @Test
  void testUpdateTransfer() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    UUID transferId = testContext.getRandomTransferByUser(userId).getTransferId();
    TransferUpdateRequestDto request = testContext.createTransferUpdateRequest(userId);
    mockServer.mockExistentAccount();
    mockServer.mockExistentCurrency(request.currency());

    ResponseEntity<TransferDto> response =
        restTemplate.exchange(
            basePath + "/transfer/%s".formatted(transferId),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getTransactions().size(), transactionRepository.findAll().size());
  }

  @Test
  void testDeleteTransfer() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    UUID transferId = testContext.getRandomTransferByUser(userId).getTransferId();

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/transfer/%s".formatted(transferId),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() - 2, transactionRepository.findAll().size());
  }

  @Test
  void testGetTransfer() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Transfer transfer = testContext.getRandomTransferByUser(userId);

    ResponseEntity<TransferDto> response =
        restTemplate.exchange(
            basePath + "/transfer/%s".formatted(transfer.getTransferId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getTransactions().size());
  }

  @Test
  void testCreateTag() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final TagRequestDto request = createTagRequest(userId);

    ResponseEntity<TagDto> response =
        restTemplate.exchange(
            basePath + "/tags",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getTags().size() + 1, tagRepository.findAll().size());
    assertEquals(request.tagName(), response.getBody().getTagName());
    assertEquals(userId, response.getBody().getUserId());
    assertEquals(request.description(), response.getBody().getDescription());
  }

  @Test
  void testUserTags() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<List<TagDto>> response =
        restTemplate.exchange(
            basePath + "/tags/users/" + userId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getUserTags(userId).size(), response.getBody().size());
  }

  @Test
  void testGetTag() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserTags(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Tag tag = testContext.getRandomUserTag(userId);

    ResponseEntity<TagDto> response =
        restTemplate.exchange(
            basePath + "/tags/" + tag.getTagId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(tag.getTagId(), response.getBody().getTagId());
  }

  @Test
  void testUpdateTag() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserTags(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Tag tag = testContext.getRandomUserTag(userId);
    TagUpdateRequestDto request = createTagUpdateRequest();

    ResponseEntity<TagDto> response =
        restTemplate.exchange(
            basePath + "/tags/" + tag.getTagId(),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(tag.getTagId(), response.getBody().getTagId());
    assertEquals(request.tagName(), response.getBody().getTagName());
    assertEquals(request.description(), response.getBody().getDescription());
  }

  @Test
  void testDeleteTag() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserTags(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Tag tag = testContext.getRandomUserTag(userId);

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/tags/" + tag.getTagId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getTags().size() - 1, tagRepository.findAll().size());
  }

  @Test
  void testCreateSubscription() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final SubscriptionRequestDto request = testContext.createSubscriptionRequest(userId);
    mockServer.mockExistentAccount();
    mockServer.mockExistentCategory();
    mockServer.mockExistentCurrency(request.currency());
    int expectedCreatedTransactions =
        request.recurrence().startDate().isBefore(LocalDate.now())
            ? (int) ChronoUnit.MONTHS.between(request.recurrence().startDate(), LocalDate.now()) + 1
            : 0;

    ResponseEntity<SubscriptionDto> response =
        restTemplate.exchange(
            basePath + "/subscriptions",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            SubscriptionDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(
        testContext.getSubscriptions().size() + 1, subscriptionRepository.findAll().size());
    assertEquals(request.userId(), response.getBody().getUserId());
    assertEquals(request.accountId(), response.getBody().getAccountId());
    assertEquals(request.categoryId(), response.getBody().getCategoryId());
    assertEquals(request.recurrence().startDate(), response.getBody().getStartDate());
    assertEquals(request.subscriptionName(), response.getBody().getSubscriptionName());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(
        request
            .amount()
            .multiply(BigDecimal.valueOf(expectedCreatedTransactions))
            .setScale(2, RoundingMode.HALF_UP),
        response.getBody().getTotalAmount().setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        testContext.getTransactions().size() + expectedCreatedTransactions,
        transactionRepository.findAll().size());
  }

  @Test
  void testGetSubscription() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserSubscription(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final Subscription subscription = testContext.getRandomSubscriptionByUser(userId);

    ResponseEntity<SubscriptionDto> response =
        restTemplate.exchange(
            basePath + "/subscriptions/" + subscription.getSubscriptionId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            SubscriptionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(subscription.getSubscriptionId(), response.getBody().getSubscriptionId());
    assertEquals(subscription.getAccountId(), response.getBody().getAccountId());
    assertEquals(subscription.getCategoryId(), response.getBody().getCategoryId());
    assertEquals(subscription.getStartDate(), response.getBody().getStartDate());
    assertEquals(subscription.getSubscriptionName(), response.getBody().getSubscriptionName());
    assertEquals(
        subscription.getAmount().setScale(2, RoundingMode.HALF_UP), response.getBody().getAmount());
    assertEquals(subscription.getCurrency(), response.getBody().getCurrency());
    assertEquals(
        subscription.getTotalAmount().setScale(2, RoundingMode.HALF_UP),
        response.getBody().getTotalAmount());
    assertEquals(
        subscription.getTransactions().size(), response.getBody().getTransactions().size());
  }

  @Test
  void testGetSubscriptions() {
    UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final List<Subscription> subscriptions = testContext.getUserSubscription(userId);

    ResponseEntity<List<SubscriptionDto>> response =
        restTemplate.exchange(
            basePath + "/subscriptions/users/" + userId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(subscriptions.size(), response.getBody().size());
  }

  @Test
  void testDeleteSubscription() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserSubscription(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Subscription subscription = testContext.getRandomSubscriptionByUser(userId);

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/subscriptions/" + subscription.getSubscriptionId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(
        testContext.getSubscriptions().size() - 1, subscriptionRepository.findAll().size());
    assertEquals(
        testContext.getTransactions().size() - subscription.getTransactions().size(),
        transactionRepository.findAll().size());
  }

  @Test
  void testUpdateSubscription() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserSubscription(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Subscription subscription = testContext.getRandomSubscriptionByUser(userId);
    SubscriptionUpdateRequestDto request = createSubscriptionUpdateRequest();
    mockServer.mockExistentAccount();
    mockServer.mockExistentCategory();

    ResponseEntity<SubscriptionDto> response =
        restTemplate.exchange(
            basePath + "/subscriptions/" + subscription.getSubscriptionId(),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            SubscriptionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(subscription.getSubscriptionId(), response.getBody().getSubscriptionId());
    assertEquals(request.accountId(), response.getBody().getAccountId());
    assertEquals(request.categoryId(), response.getBody().getCategoryId());
    assertEquals(request.subscriptionName(), response.getBody().getSubscriptionName());
    assertEquals(request.amount(), response.getBody().getAmount());
  }
}
