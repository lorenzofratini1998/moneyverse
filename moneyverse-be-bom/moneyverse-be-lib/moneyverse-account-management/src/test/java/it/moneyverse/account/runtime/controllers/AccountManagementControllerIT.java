package it.moneyverse.account.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.utils.AccountIntegrationTest;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.constants.DatasourcePropertiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;

class AccountManagementControllerIT extends AccountIntegrationTest {

  @Value("${moneyverse.account-management.base-path.v1}")
  private String accountManagementBasePath;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();

  static {
    Startables.deepStart(postgresContainer, keycloakContainer).join();
  }

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    registry.add(DatasourcePropertiesConstants.DRIVER_CLASS_NAME, postgresContainer::getDriverClassName);
    registry.add(DatasourcePropertiesConstants.URL, postgresContainer::getJdbcUrl);
    registry.add(DatasourcePropertiesConstants.USERNAME, postgresContainer::getUsername);
    registry.add(DatasourcePropertiesConstants.PASSWORD, postgresContainer::getPassword);
  }

  @Test
  void testCreateAccount_Success() {
    final AccountRequestDto request = createAccountForUser(testContext.getRandomUser().getUserId());
    final String url = accountManagementBasePath + "/accounts";
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(url, request, AccountDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getAccounts().size() + 1, accountRepository.findAll().size());
  }

}
