package it.moneyverse.account.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountMapperTest {

  @Test
  void testToEntity_NullDto() {
    assertNull(AccountMapper.toAccount(null));
  }

  @Test
  void testToEntity_ValidDto() {
    AccountRequestDto dto = new AccountRequestDto(
        "569f67de-36e6-4552-ac54-e52085109818",
        "Savings",
        BigDecimal.valueOf(1000.0),
        BigDecimal.valueOf(2000.0),
        AccountCategoryEnum.SAVINGS,
        "Personal Savings Account",
        Boolean.TRUE
    );
    Account account = AccountMapper.toAccount(dto);

    assertEquals("569f67de-36e6-4552-ac54-e52085109818", account.getUsername());
    assertEquals("Savings", account.getAccountName());
    assertEquals(BigDecimal.valueOf(1000.0), account.getBalance());
    assertEquals(BigDecimal.valueOf(2000.0), account.getBalanceTarget());
    assertEquals(AccountCategoryEnum.SAVINGS, account.getAccountCategory());
    assertEquals("Personal Savings Account", account.getAccountDescription());
    assertTrue(account.isDefault());
  }

  @Test
  void testToDto_NullEntity() {
    assertNull(AccountMapper.toAccountDto(null));
  }

  @Test
  void testToDto_ValidEntity() {
    Account account = new Account();
    account.setAccountId(UUID.fromString("f740cf0c-cc87-4de8-bcc9-040a6d26dff6"));
    account.setUsername("569f67de-36e6-4552-ac54-e52085109818");
    account.setAccountName("Savings");
    account.setBalance(BigDecimal.valueOf(1000.0));
    account.setBalanceTarget(BigDecimal.valueOf(2000.0));
    account.setAccountCategory(AccountCategoryEnum.SAVINGS);
    account.setAccountDescription("Personal Savings Account");
    account.setDefault(true);

    AccountDto accountDto = AccountMapper.toAccountDto(account);

    assertEquals(UUID.fromString("f740cf0c-cc87-4de8-bcc9-040a6d26dff6"), accountDto.getAccountId());
    assertEquals("569f67de-36e6-4552-ac54-e52085109818", accountDto.getUsername());
    assertEquals("Savings", accountDto.getAccountName());
    assertEquals(BigDecimal.valueOf(1000.0), accountDto.getBalance());
    assertEquals(BigDecimal.valueOf(2000.0), accountDto.getBalanceTarget());
    assertEquals(AccountCategoryEnum.SAVINGS, accountDto.getAccountCategory());
    assertEquals("Personal Savings Account", accountDto.getAccountDescription());
    assertTrue(accountDto.isDefault());
  }

}
