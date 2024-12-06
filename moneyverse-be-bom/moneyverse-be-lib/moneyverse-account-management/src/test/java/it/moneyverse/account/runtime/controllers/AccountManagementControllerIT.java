package it.moneyverse.account.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertiesHelper;
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
        AccountTestContext.builder()
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
    final String username = testContext.getRandomUser().getUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final AccountRequestDto request = testContext.createAccountForUser(username);
    mockUserService.mockExistentUser();
    final AccountDto expected = testContext.toAccountDto(request);

    ResponseEntity<AccountDto> response =
        restTemplate.postForEntity(
            basePath + "/accounts", new HttpEntity<>(request, headers), AccountDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getAccountsCount() + 1, accountRepository.findAll().size());
    compareActualWithExpectedAccount(response.getBody(), expected);
  }

  @Test
  void testGetAccountsAdminRole_Success() {
    final String admin = testContext.getAdminUser().getUsername();
    final AccountCriteria criteria = testContext.createAccountFilters();
    final List<AccountModel> expected = testContext.filterAccounts(criteria);

    ResponseEntity<List<AccountDto>> response = testGetAccounts(admin, criteria);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  void testGetAccountsUserRole_Success() {
    final String username = testContext.getRandomUser().getUsername();
    final AccountCriteria criteria = testContext.createAccountFilters();
    criteria.setUsername(username);
    final List<AccountModel> expected = testContext.filterAccounts(criteria);

    ResponseEntity<List<AccountDto>> response = testGetAccounts(username, criteria);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  void testGetAccountsUserRole_Unauthorized() {
    final String username = testContext.getRandomUser().getUsername();
    final AccountCriteria criteria = testContext.createAccountFilters();
    criteria.setUsername(null);

    ResponseEntity<List<AccountDto>> response = testGetAccounts(username, criteria);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  private ResponseEntity<List<AccountDto>> testGetAccounts(
      String username, AccountCriteria criteria) {
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    return restTemplate.exchange(
        testContext.createUri(basePath + "/accounts", criteria),
        HttpMethod.GET,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<>() {});
  }

  private void compareActualWithExpectedAccount(AccountDto actual, AccountDto expected) {
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(expected);
  }
}
