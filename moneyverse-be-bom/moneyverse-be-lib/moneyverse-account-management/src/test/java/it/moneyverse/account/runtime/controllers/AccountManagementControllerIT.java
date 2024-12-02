package it.moneyverse.account.runtime.controllers;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.helper.AccountTestHelper;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import it.moneyverse.test.utils.properties.TestPropertiesHelper;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class AccountManagementControllerIT extends AbstractIntegrationTest {

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @RegisterExtension static GrpcMockUserService mockUserService = new GrpcMockUserService();
  @Autowired protected AccountRepository accountRepository;

  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    TestPropertiesHelper.setupPostgresProperties(registry, postgresContainer);
    TestPropertiesHelper.setupKeycloakProperties(registry, keycloakContainer);
    mockUserService.setupProperties(registry);
  }

  @BeforeAll
  static void beforeAll() {
    TestContext testContext =
        TestContext.builder()
            .withStrategy(TestModelStrategyEnum.RANDOM)
            .withTestUsers()
            .withTestAccount()
            .withKeycloak(keycloakContainer)
            .withScriptMetadata(new ScriptMetadata(tempDir, Account.class))
            .build();
    testModel = testContext.getModel();
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateAccount_Success() {
    final UserRepresentation user = keycloakContainer.getRandomUser(TEST_REALM);
    headers.setBearerAuth(getBearerToken(user.getUsername()));
    final AccountRequestDto request = createAccountForUser(user.getUsername());
    mockUserService.mockExistentUser();

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testModel.getAccounts().size() + 1, accountRepository.findAll().size());
  }

  @ParameterizedTest
  @MethodSource("invalidAccountRequestProvider")
  void testCreateAccount_BadRequestValidation(
      Function<String, AccountRequestDto> requestGenerator) {
    final UserRepresentation user = keycloakContainer.getRandomUser(TEST_REALM);
    headers.setBearerAuth(getBearerToken(user.getUsername()));
    final AccountRequestDto request = requestGenerator.apply(user.getId());
    mockUserService.mockExistentUser();

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(testModel.getAccounts().size(), accountRepository.findAll().size());
  }

  @Test
  void testCreateAccount_AccountAlreadyExists() {
    final UserRepresentation user = keycloakContainer.getRandomUser(TEST_REALM);
    headers.setBearerAuth(getBearerToken(user.getUsername()));
    final AccountRequestDto request = createExistentAccountForUser(user.getUsername());
    mockUserService.mockExistentUser();

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(testModel.getAccounts().size(), accountRepository.findAll().size());
  }

  @Test
  void testCreateAccount_AccountNotFound() {
    final UserRepresentation user = keycloakContainer.getRandomUser(TEST_REALM);
    headers.setBearerAuth(getBearerToken(user.getUsername()));
    final AccountRequestDto request = createExistentAccountForUser(user.getUsername());
    mockUserService.mockNonExistentUser();

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private String getBearerToken(String username) {
    return keycloakContainer.getTestAuthenticationToken(
        testModel.getUserCredential(username), TEST_REALM);
  }

  private AccountRequestDto createAccountForUser(String username) {
    return AccountTestHelper.toAccountRequest(
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class));
  }

  private static Stream<Function<String, AccountRequestDto>> invalidAccountRequestProvider() {
    return Stream.of(
        AccountManagementControllerIT::createAccountWithNullUsername,
        AccountManagementControllerIT::createAccountWithNullAccountName,
        AccountManagementControllerIT::createAccountWithNullAccountCategory,
        AccountManagementControllerIT::createAccountWithExceedUsername);
  }

  private static AccountRequestDto createAccountWithNullUsername(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class);
    account.setUsername(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithNullAccountName(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class);
    account.setAccountName(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithNullAccountCategory(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class);
    account.setAccountCategory(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithExceedUsername(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class);
    account.setUsername(RandomUtils.randomString(100));
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createExistentAccountForUser(String username) {
    AccountModel randomExistingAccount = testModel.getRandomAccount(username);
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testModel.getAccounts().size()), Account.class);
    account.setAccountName(randomExistingAccount.getAccountName());
    return AccountTestHelper.toAccountRequest(account);
  }
}
