package it.moneyverse.account.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.utils.AccountIntegrationTest;
import it.moneyverse.test.annotations.testcontainers.PostgreSQLContainer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@PostgreSQLContainer
class AccountManagementControllerIT extends AccountIntegrationTest {

  @Value("${moneyverse.account-management.base-path.v1}")
  private String accountManagementBasePath;

  @Test
  void testCreateAccount_Success() {
    final AccountRequestDto request = createAccountForUser(testContext.getRandomUser().getUserId());
    final String url = accountManagementBasePath + "/accounts";
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(url, request, AccountDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getAccounts().size() + 1, accountRepository.findAll().size());
  }

}
