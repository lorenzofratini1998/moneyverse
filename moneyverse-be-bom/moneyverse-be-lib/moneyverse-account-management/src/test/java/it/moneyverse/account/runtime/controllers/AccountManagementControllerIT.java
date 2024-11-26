package it.moneyverse.account.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.AccountTestUtils;
import it.moneyverse.enums.AccountCategoryEnum;
import it.moneyverse.model.entities.Account;
import it.moneyverse.test.annotations.testcontainers.PostgreSQLContainer;
import it.moneyverse.test.model.IntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.SQLTestUtils;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@PostgreSQLContainer
public class AccountManagementControllerIT extends IntegrationTest {

  private static final String ACCOUNT_MANAGEMENT_BASE_PATH = "/accountsManagement/api/v1";

  private final List<Account> accounts;

  @Autowired
  private TestRestTemplate restTemplate;

  public AccountManagementControllerIT() {
    super();
    accounts = AccountTestUtils.toAccount(testContext.getAccounts());
    SQLTestUtils.saveScriptFile(tempDir, accounts,
        Account.class);
  }

  @Test
  void testCreateAccount_Success() {
    final AccountRequestDto request = createRequest();
    ResponseEntity<AccountDto> response = restTemplate.postForEntity(
        ACCOUNT_MANAGEMENT_BASE_PATH + "/accounts",
        request, AccountDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }


  private AccountRequestDto createRequest() {
    return new AccountRequestDto(
        testContext.getUsers().get(RandomUtils.randomInteger(0, testContext.getUsers().size()))
            .getUserId(),
        "Account NEW",
        BigDecimal.ZERO,
        null,
        RandomUtils.randomEnum(AccountCategoryEnum.class),
        "Account Descrition NEW",
        Boolean.FALSE
    );
  }

}
