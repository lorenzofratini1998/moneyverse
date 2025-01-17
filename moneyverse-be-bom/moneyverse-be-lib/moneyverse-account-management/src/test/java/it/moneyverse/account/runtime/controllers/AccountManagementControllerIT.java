package it.moneyverse.account.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
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
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired protected AccountRepository accountRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        new AccountTestContext().generateScript(tempDir).insertUsersIntoKeycloak(keycloakContainer);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateAccount() {
    final String username = testContext.getRandomUser().getUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = testContext.createAccountForUser(username);
    mockServer.mockExistentUser();
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
    final UserModel user = testContext.getRandomAdminOrUser();
    final AccountCriteria criteria = testContext.createAccountFilters();
    if (user.getRole().equals(UserRoleEnum.USER)) {
      criteria.setUsername(user.getUsername());
    }
    final List<Account> expected = testContext.filterAccounts(criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));
    ResponseEntity<List<AccountDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/accounts", criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  void testGetAccount() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID accountId = testContext.getRandomAccount(user.getUsername()).getAccountId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

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
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID accountId = testContext.getRandomAccount(user.getUsername()).getAccountId();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            null,
            RandomUtils.randomBigDecimal(),
            null,
            null,
            RandomUtils.randomString(25),
            RandomUtils.randomEnum(CurrencyEnum.class),
            RandomUtils.randomBoolean());

    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));
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
    assertEquals(1, accountRepository.findDefaultAccountsByUser(user.getUsername()).size());
  }

  @Test
  void testDeleteAccount_Success() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID accountId = testContext.getRandomAccount(user.getUsername()).getAccountId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

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
    final UserModel user = testContext.getRandomAdminOrUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

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
