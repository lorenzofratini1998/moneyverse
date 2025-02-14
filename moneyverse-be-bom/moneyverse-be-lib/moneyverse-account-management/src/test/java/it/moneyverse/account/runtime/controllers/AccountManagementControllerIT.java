package it.moneyverse.account.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.extensions.testcontainers.RedisContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class AccountManagementControllerIT extends AbstractIntegrationTest {

  protected static AccountTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static RedisContainer redisContainer = new RedisContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired protected AccountRepository accountRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withRedis(redisContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new AccountTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateAccount() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final AccountRequestDto request = testContext.createAccountForUser(userId);
    mockServer.mockExistentCurrency(request.currency());
    AccountDto expected = testContext.getExpectedAccountDto(request);

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getAccountsCount() + 1, accountRepository.findAll().size());
    compareActualWithExpectedAccount(response.getBody(), expected);
  }

  @Test
  void testGetAccounts() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final AccountCriteria criteria = testContext.createAccountFilters();
    final List<Account> expected = testContext.filterAccounts(userId, criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    ResponseEntity<List<AccountDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/accounts/users/" + userId, criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  void testGetAccount() {
    final UserModel user = testContext.getRandomUser();
    final UUID accountId = testContext.getRandomAccount(user.getUserId()).getAccountId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<AccountDto> response =
        restTemplate.exchange(
            basePath + "/accounts/" + accountId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            AccountDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(accountId, response.getBody().getAccountId());
  }

  @Test
  void testUpdateAccount() {
    final UserModel user = testContext.getRandomUser();
    final UUID accountId = testContext.getRandomAccount(user.getUserId()).getAccountId();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            null,
            RandomUtils.randomBigDecimal(),
            null,
            null,
            RandomUtils.randomString(25),
            RandomUtils.randomString(3).toUpperCase(),
            RandomUtils.randomBoolean());

    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));
    mockServer.mockExistentCurrency(request.currency());

    ResponseEntity<AccountDto> response =
        restTemplate.exchange(
            basePath + "/accounts/" + accountId,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            AccountDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(request.balance(), response.getBody().getBalance());
    assertEquals(request.accountDescription(), response.getBody().getAccountDescription());
    assertEquals(request.isDefault(), response.getBody().isDefault());
    assertEquals(1, accountRepository.findDefaultAccountsByUserId(user.getUserId()).size());
  }

  @Test
  void testDeleteAccount_Success() {
    final UserModel user = testContext.getRandomUser();
    final UUID accountId = testContext.getRandomAccount(user.getUserId()).getAccountId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/accounts/" + accountId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getAccountsCount() - 1, accountRepository.count());
  }

  @Test
  void testGetAccountCategories() {
    final UserModel user = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<List<AccountCategoryDto>> result =
        restTemplate.exchange(
            basePath + "/accounts/categories",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result.getBody());
    assertEquals(testContext.getCategories().size(), result.getBody().size());
  }

  private void compareActualWithExpectedAccount(AccountDto actual, AccountDto expected) {
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(expected);
  }
}
