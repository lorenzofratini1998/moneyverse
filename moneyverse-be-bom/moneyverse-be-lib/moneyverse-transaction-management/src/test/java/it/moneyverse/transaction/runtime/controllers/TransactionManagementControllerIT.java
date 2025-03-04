package it.moneyverse.transaction.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.extensions.testcontainers.RedisContainer;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.transaction.model.SubscriptionTestFactory;
import it.moneyverse.transaction.model.TagTestFactory;
import it.moneyverse.transaction.model.TransactionTestContext;
import it.moneyverse.transaction.model.TransactionTestFactory;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

@MoneyverseTest
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
        .withKafkaContainer(kafkaContainer)
        .withFlywayTestDirectory(tempDir);
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
    mockServer.mockExistentBudget(
        request.transactions().getFirst().categoryId(), request.transactions().getFirst().date());
    mockServer.mockExistentCurrency(request.transactions().getFirst().currency());
    mockServer.mockUserPreference(request.transactions().getFirst().currency());
    mockServer.mockExchangeRate(TestFactory.fakeExchangeRate());

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
    TransactionRequestItemDto expected = request.transactions().getFirst();
    assertEquals(request.userId(), actual.getUserId());
    assertEquals(expected.accountId(), actual.getAccountId());
    assertEquals(expected.categoryId(), actual.getCategoryId());
    assertNotNull(actual.getBudgetId());
    assertEquals(expected.date(), actual.getDate());
    assertEquals(expected.description(), actual.getDescription());
    assertEquals(expected.amount(), actual.getAmount());
    assertNotNull(actual.getNormalizedAmount());
    assertEquals(expected.currency(), actual.getCurrency());
    assertEquals(expected.tags().size(), actual.getTags().size());
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
    final Transaction transaction = testContext.getRandomTransaction(userId);
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<TransactionDto> response =
        restTemplate.exchange(
            basePath + "/transactions/" + transaction.getTransactionId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            TransactionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transaction.getTransactionId(), response.getBody().getTransactionId());
    assertEquals(transaction.getAccountId(), response.getBody().getAccountId());
    assertEquals(transaction.getCategoryId(), response.getBody().getCategoryId());
    assertEquals(transaction.getBudgetId(), response.getBody().getBudgetId());
    assertEquals(round(transaction.getAmount()), response.getBody().getAmount());
    assertEquals(transaction.getDate(), response.getBody().getDate());
    assertEquals(transaction.getDescription(), response.getBody().getDescription());
    assertEquals(
        round(transaction.getNormalizedAmount()), response.getBody().getNormalizedAmount());
    assertEquals(transaction.getCurrency(), response.getBody().getCurrency());
    assertEquals(transaction.getTags().size(), response.getBody().getTags().size());
    if (transaction.getTransfer() != null) {
      assertEquals(transaction.getTransfer().getTransferId(), response.getBody().getTransferId());
    }
    if (transaction.getSubscription() != null) {
      assertEquals(
          transaction.getSubscription().getSubscriptionId(),
          response.getBody().getSubscriptionId());
    }
  }

  @Test
  void testUpdateTransaction() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final Transaction transaction = testContext.getRandomTransaction(userId);
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.builder()
            .withTags(testContext.getRandomTag(userId))
            .build();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency(request.currency());
    mockServer.mockExistentBudget(request.categoryId(), request.date());
    mockServer.mockUserPreference(request.currency());
    mockServer.mockExchangeRate(TestFactory.fakeExchangeRate());
    mockServer.mockExistentAccount();
    mockServer.mockExistentCategory();

    ResponseEntity<TransactionDto> response =
        restTemplate.exchange(
            basePath + "/transactions/" + transaction.getTransactionId(),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            TransactionDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transaction.getTransactionId(), response.getBody().getTransactionId());
    assertEquals(request.accountId(), response.getBody().getAccountId());
    assertEquals(request.categoryId(), response.getBody().getCategoryId());
    assertEquals(request.date(), response.getBody().getDate());
    assertEquals(request.description(), response.getBody().getDescription());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.currency(), response.getBody().getCurrency());
    if (request.tags() != null && !request.tags().isEmpty()) {
      assertEquals(request.tags().size(), response.getBody().getTags().size());
    }
    if (transaction.getTransfer() != null) {
      assertEquals(transaction.getTransfer().getTransferId(), response.getBody().getTransferId());
    }
    if (transaction.getSubscription() != null) {
      assertEquals(
          transaction.getSubscription().getSubscriptionId(),
          response.getBody().getSubscriptionId());
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
    mockServer.mockUserPreference(request.currency());
    mockServer.mockExchangeRate(TestFactory.fakeExchangeRate());

    ResponseEntity<TransferDto> response =
        restTemplate.exchange(
            basePath + "/transfer",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getTransactions().size() + 2, transactionRepository.findAll().size());
    assertNotNull(response.getBody());
    assertEquals(request.userId(), response.getBody().getUserId());
    assertEquals(request.date(), response.getBody().getDate());
    assertEquals(round(request.amount()), round(response.getBody().getAmount()));
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(request.userId(), response.getBody().getTransactionFrom().getUserId());
    assertEquals(request.fromAccount(), response.getBody().getTransactionFrom().getAccountId());
    assertEquals(request.date(), response.getBody().getTransactionFrom().getDate());
    assertEquals(request.amount().negate(), response.getBody().getTransactionFrom().getAmount());
    assertEquals(request.currency(), response.getBody().getTransactionFrom().getCurrency());
    assertEquals(request.userId(), response.getBody().getTransactionTo().getUserId());
    assertEquals(request.toAccount(), response.getBody().getTransactionTo().getAccountId());
    assertEquals(request.date(), response.getBody().getTransactionTo().getDate());
    assertEquals(request.amount(), response.getBody().getTransactionTo().getAmount());
    assertEquals(request.currency(), response.getBody().getTransactionTo().getCurrency());
  }

  @Test
  void testUpdateTransfer() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Transfer transfer = testContext.getRandomTransferByUser(userId);
    TransferUpdateRequestDto request = testContext.createTransferUpdateRequest(userId);
    mockServer.mockExistentAccount();
    mockServer.mockExistentCurrency(request.currency());

    ResponseEntity<TransferDto> response =
        restTemplate.exchange(
            basePath + "/transfer/%s".formatted(transfer.getTransferId()),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getTransactions().size(), transactionRepository.findAll().size());
    assertEquals(transfer.getTransferId(), response.getBody().getTransferId());
    assertEquals(transfer.getUserId(), response.getBody().getUserId());
    assertEquals(request.date(), response.getBody().getDate());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(request.fromAccount(), response.getBody().getTransactionFrom().getAccountId());
    assertEquals(request.date(), response.getBody().getTransactionFrom().getDate());
    assertEquals(request.amount().negate(), response.getBody().getTransactionFrom().getAmount());
    assertEquals(request.currency(), response.getBody().getTransactionFrom().getCurrency());
    assertEquals(request.toAccount(), response.getBody().getTransactionTo().getAccountId());
    assertEquals(request.date(), response.getBody().getTransactionTo().getDate());
    assertEquals(request.amount(), response.getBody().getTransactionTo().getAmount());
    assertEquals(request.currency(), response.getBody().getTransactionTo().getCurrency());
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
    assertEquals(transfer.getTransferId(), response.getBody().getTransferId());
    assertEquals(transfer.getDate(), response.getBody().getDate());
    assertEquals(round(transfer.getAmount()), response.getBody().getAmount());
    assertEquals(transfer.getCurrency(), response.getBody().getCurrency());
    assertEquals(
        transfer.getTransactionFrom().getTransactionId(),
        response.getBody().getTransactionFrom().getTransactionId());
    assertEquals(
        transfer.getTransactionFrom().getTransfer().getTransferId(),
        response.getBody().getTransferId());
    assertEquals(
        round(transfer.getTransactionFrom().getAmount()),
        response.getBody().getTransactionFrom().getAmount());
    assertEquals(
        transfer.getTransactionFrom().getCurrency(),
        response.getBody().getTransactionFrom().getCurrency());
    assertEquals(
        transfer.getTransactionFrom().getDate(), response.getBody().getTransactionFrom().getDate());
    assertEquals(
        transfer.getTransactionTo().getTransactionId(),
        response.getBody().getTransactionTo().getTransactionId());
    assertEquals(
        transfer.getTransactionTo().getTransfer().getTransferId(),
        response.getBody().getTransferId());
    assertEquals(
        round(transfer.getTransactionTo().getAmount()),
        response.getBody().getTransactionTo().getAmount());
    assertEquals(
        transfer.getTransactionTo().getCurrency(),
        response.getBody().getTransactionTo().getCurrency());
    assertEquals(
        transfer.getTransactionTo().getDate(), response.getBody().getTransactionTo().getDate());
  }

  @Test
  void testCreateTag() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final TagRequestDto request = TagTestFactory.fakeTagRequest(userId);

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
    assertEquals(tag.getUserId(), response.getBody().getUserId());
    assertEquals(tag.getTagName(), response.getBody().getTagName());
    assertEquals(tag.getDescription(), response.getBody().getDescription());
  }

  @Test
  void testUpdateTag() {
    UUID userId = testContext.getRandomUser().getUserId();
    while (testContext.getUserTags(userId).isEmpty()) {
      userId = testContext.getRandomUser().getUserId();
    }
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    Tag tag = testContext.getRandomUserTag(userId);
    TagUpdateRequestDto request = TagTestFactory.fakeTagUpdateRequest();

    ResponseEntity<TagDto> response =
        restTemplate.exchange(
            basePath + "/tags/" + tag.getTagId(),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(tag.getTagId(), response.getBody().getTagId());
    assertEquals(tag.getUserId(), response.getBody().getUserId());
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
    mockServer.mockExistentBudget(request.categoryId(), request.recurrence().startDate());
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
    assertEquals(request.subscriptionName(), response.getBody().getSubscriptionName());
    assertEquals(round(request.amount()), round(response.getBody().getAmount()));
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(request.recurrence().startDate(), response.getBody().getStartDate());
    assertEquals(request.recurrence().endDate(), response.getBody().getEndDate());
    assertEquals(request.recurrence().recurrenceRule(), response.getBody().getRecurrenceRule());
    assertEquals(
        round(request.amount().multiply(BigDecimal.valueOf(expectedCreatedTransactions))),
        round(response.getBody().getTotalAmount()));
    assertEquals(
        testContext.getTransactions().size() + expectedCreatedTransactions,
        transactionRepository.findAll().size());
    response
        .getBody()
        .getTransactions()
        .forEach(
            transaction -> {
              assertEquals(round(request.amount()), round(transaction.getAmount()));
              assertEquals(request.categoryId(), transaction.getCategoryId());
              assertEquals(request.subscriptionName(), transaction.getDescription());
              assertEquals(request.currency(), transaction.getCurrency());
            });
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
    assertEquals(round(subscription.getAmount()), response.getBody().getAmount());
    assertEquals(subscription.getCurrency(), response.getBody().getCurrency());
    assertEquals(round(subscription.getTotalAmount()), response.getBody().getTotalAmount());
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
    SubscriptionUpdateRequestDto request = SubscriptionTestFactory.fakeSubscriptionUpdateRequest();
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
    response
        .getBody()
        .getTransactions()
        .forEach(
            transaction -> {
              assertEquals(transaction.getCategoryId(), request.categoryId());
            });
  }
}
