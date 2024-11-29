package it.moneyverse.account.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountIntegrationTest;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.utils.constants.DatasourcePropertiesConstants;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class AccountManagementControllerIT extends AccountIntegrationTest {

  @Value("${moneyverse.account-management.base-path.v1}")
  private String accountManagementBasePath;

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected AccountRepository accountRepository;

  @Container
  static PostgresContainer postgresContainer = new PostgresContainer();
  @Container
  static KeycloakContainer keycloakContainer = new KeycloakContainer();

  @BeforeAll
  static void beforeAll() {
    TestContext testContext = TestContext.builder()
        .withStrategy(TestModelStrategyEnum.RANDOM)
        .withTestUsers()
        .withTestAccount()
        .withKeycloak(keycloakContainer)
        .withScriptMetadata(new ScriptMetadata(tempDir, Account.class))
        .build();
    testModel = testContext.getModel();
  }

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    registry.add(DatasourcePropertiesConstants.DRIVER_CLASS_NAME,
        postgresContainer::getDriverClassName);
    registry.add(DatasourcePropertiesConstants.URL, postgresContainer::getJdbcUrl);
    registry.add(DatasourcePropertiesConstants.USERNAME, postgresContainer::getUsername);
    registry.add(DatasourcePropertiesConstants.PASSWORD, postgresContainer::getPassword);
  }

  @Test
  void testCreateAccount_Success() {
    final AccountRequestDto request = createAccountForUser(testModel.getRandomUser().getUserId());
    final String url = accountManagementBasePath + "/accounts";
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(url, request,
        AccountDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testModel.getAccounts().size() + 1, accountRepository.findAll().size());
  }

  @ParameterizedTest
  @MethodSource("invalidAccountRequestProvider")
  void testCreateAccount_BadRequestValidation(Function<UUID, AccountRequestDto> requestGenerator) {
    final AccountRequestDto request = requestGenerator.apply(
        testModel.getRandomUser().getUserId());
    final String url = accountManagementBasePath + "/accounts";
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(url, request,
        AccountDto.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(testModel.getAccounts().size(), accountRepository.findAll().size());
  }

  @Test
  void testCreateAccount_AccountAlreadyExists() {
    final AccountRequestDto request = createAccountRequestForExistentAccount(
        testModel.getRandomUser().getUserId());
    final String url = accountManagementBasePath + "/accounts";
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(url, request,
        AccountDto.class);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(testModel.getAccounts().size(), accountRepository.findAll().size());
  }


}
