package it.moneyverse.account.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.QAccount;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.helper.AccountCriteriaRandomGenerator;
import it.moneyverse.account.utils.helper.AccountTestHelper;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.utils.JsonUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
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
    testContext =
        TestContext.builder()
            .withStrategy(TestModelStrategyEnum.RANDOM)
            .withTestUsers()
            .withTestAccount()
            .withKeycloak(keycloakContainer)
            .withScriptMetadata(new ScriptMetadata(tempDir, Account.class))
            .build();
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateAccount_Success() {
    final String username = testContext.getRandomUserOrAdminUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = createAccountForUser(username);
    mockUserService.mockExistentUser();
    final AccountDto expected = AccountTestHelper.toAccountDto(request);

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(
        testContext.getModel().getAccounts().size() + 1, accountRepository.findAll().size());
    compareActualWithExpectedAccount(response.getBody(), expected);
  }

  @ParameterizedTest
  @MethodSource("invalidAccountRequestProvider")
  void testCreateAccount_BadRequestValidation(
      Function<String, AccountRequestDto> requestGenerator) {
    final String username = testContext.getRandomUserOrAdminUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = requestGenerator.apply(username);
    mockUserService.mockExistentUser();

    ResponseEntity<ErrorDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), ErrorDto.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(testContext.getModel().getAccounts().size(), accountRepository.findAll().size());
  }

  @Test
  void testCreateAccount_AccountAlreadyExists() {
    final String username = testContext.getRandomUserOrAdminUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = createExistentAccountForUser(username);
    mockUserService.mockExistentUser();

    ResponseEntity<ErrorDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), ErrorDto.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(testContext.getModel().getAccounts().size(), accountRepository.findAll().size());
  }

  @Test
  void testCreateAccount_AccountNotFound() {
    final String username = testContext.getRandomUserOrAdminUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = createExistentAccountForUser(username);
    mockUserService.mockNonExistentUser();

    ResponseEntity<ErrorDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), ErrorDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testGetAccountsAdminRole_Success() {
    final String admin = testContext.getAdminUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(admin));
    final AccountCriteria criteria =
        new AccountCriteriaRandomGenerator(testContext.getModel()).generate();
    final List<AccountModel> expected = filterByAccountCriteria(criteria);

    ResponseEntity<List<AccountDto>> response =
        restTemplate.exchange(
            createUri(basePath + "/accounts", criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  private AccountRequestDto createAccountForUser(String username) {
    return AccountTestHelper.toAccountRequest(
        MapperTestHelper.map(
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class));
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
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class);
    account.setUsername(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithNullAccountName(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class);
    account.setAccountName(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithNullAccountCategory(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class);
    account.setAccountCategory(null);
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createAccountWithExceedUsername(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class);
    account.setUsername(RandomUtils.randomString(100));
    return AccountTestHelper.toAccountRequest(account);
  }

  private static AccountRequestDto createExistentAccountForUser(String username) {
    AccountModel randomExistingAccount = testContext.getModel().getRandomAccount(username);
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, testContext.getModel().getAccounts().size()), Account.class);
    account.setAccountName(randomExistingAccount.getAccountName());
    return AccountTestHelper.toAccountRequest(account);
  }

  private void compareActualWithExpectedAccount(AccountDto actual, AccountDto expected) {
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(expected);
  }

  private List<AccountModel> filterByAccountCriteria(AccountCriteria criteria) {
    return testContext.getModel().getAccounts().stream()
        .filter(
            account ->
                criteria.getUsername().isEmpty()
                    || account.getUsername().equals(criteria.getUsername().get()))
        .filter(
            account ->
                criteria.getBalance().isEmpty()
                    || (account.getBalance().compareTo(criteria.getBalance().get().getLower().get())
                            >= 0)
                        && (account
                                .getBalance()
                                .compareTo(criteria.getBalance().get().getUpper().get())
                            <= 0))
        .filter(
            account ->
                criteria.getBalanceTarget().isEmpty()
                    || (account
                                .getBalance()
                                .compareTo(criteria.getBalanceTarget().get().getLower().get())
                            >= 0)
                        && (account
                                .getBalance()
                                .compareTo(criteria.getBalanceTarget().get().getUpper().get())
                            <= 0))
        .filter(
            account ->
                criteria.getAccountCategory().isEmpty()
                    || account.getAccountCategory() == criteria.getAccountCategory().get())
        .filter(
            account ->
                criteria.getDefault().isEmpty()
                    || account.isDefault().compareTo(criteria.getDefault().get()) == 0)
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .collect(Collectors.toList());
  }

  private String createUri(String path, AccountCriteria criteria) {
    UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(path);
    QAccount account = QAccount.account;
    criteria
        .getUsername()
        .ifPresent(username -> uri.queryParam(account.username.toString(), username));
    criteria.getBalance().ifPresent(balance -> {
      balance.getLower().ifPresent(lower -> uri.queryParam(account.balance.toString(), balance.getLower().get()));
      balance.getUpper().ifPresent(upper -> uri.queryParam(account.balance.toString(), balance.getUpper().get()));
    });
    criteria.getBalanceTarget().ifPresent(balanceTarget -> {
      balanceTarget.getLower().ifPresent(lower -> uri.queryParam(account.balanceTarget.toString(), balanceTarget.getLower().get()));
      balanceTarget.getUpper().ifPresent(upper -> uri.queryParam(account.balanceTarget.toString(), balanceTarget.getUpper().get()));
    });
    criteria.getDefault().ifPresent(isDefault -> uri.queryParam(account.isDefault.toString(), isDefault));
    uri.queryParam("page.offset", criteria.getPage().getOffset());
    uri.queryParam("page.limit", criteria.getPage().getLimit());
    uri.queryParam("sort.attribute", criteria.getSort().getAttribute());
    uri.queryParam("sort.direction", criteria.getSort().getDirection());
    return uri.build().toUriString();
  }
}
