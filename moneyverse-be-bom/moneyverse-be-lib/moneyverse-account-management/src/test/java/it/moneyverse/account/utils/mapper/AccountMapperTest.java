package it.moneyverse.account.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit test for {@link AccountMapper} */
class AccountMapperTest {

  @Test
  void testToAccountEntity_NullAccountRequest() {
    assertNull(AccountMapper.toAccount(null));
  }

  @Test
  void testToAccountEntity_ValidAccountRequest() {
    AccountRequestDto request =
        new AccountRequestDto(
            RandomUtils.randomString(25),
            RandomUtils.randomString(25),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(25));

    Account account = AccountMapper.toAccount(request);

    assertEquals(request.username(), account.getUsername());
    assertEquals(request.accountName(), account.getAccountName());
    assertEquals(request.balance(), account.getBalance());
    assertEquals(request.balanceTarget(), account.getBalanceTarget());
    assertEquals(request.accountCategory(), account.getAccountCategory());
    assertEquals(request.accountDescription(), account.getAccountDescription());
  }

  @Test
  void testToAccountDto_NullAccountEntity() {
    assertNull(AccountMapper.toAccountDto((Account) null));
  }

  @Test
  void testToAccountDto_ValidAccountEntity() {
    Account account = createAccount();

    AccountDto accountDto = AccountMapper.toAccountDto(account);

    assertEquals(account.getAccountId(), accountDto.getAccountId());
    assertEquals(account.getUsername(), accountDto.getUsername());
    assertEquals(account.getAccountName(), accountDto.getAccountName());
    assertEquals(account.getBalance(), accountDto.getBalance());
    assertEquals(account.getBalanceTarget(), accountDto.getBalanceTarget());
    assertEquals(account.getAccountCategory(), accountDto.getAccountCategory());
    assertEquals(account.getAccountDescription(), accountDto.getAccountDescription());
    assertEquals(account.isDefault(), accountDto.isDefault());
  }

  @Test
  void testToAccountDtoList_EmptyEntityList() {
    assertEquals(Collections.emptyList(), AccountMapper.toAccountDto(new ArrayList<>()));
  }

  @Test
  void testToAccountDtoList_NonEmptyEntityList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<Account> accounts = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      accounts.add(createAccount());
    }

    List<AccountDto> accountDtos = AccountMapper.toAccountDto(accounts);

    for (int i = 0; i < entitiesCount; i++) {
      Account account = accounts.get(i);
      AccountDto accountDto = accountDtos.get(i);

      assertEquals(account.getAccountId(), accountDto.getAccountId());
      assertEquals(account.getUsername(), accountDto.getUsername());
      assertEquals(account.getAccountName(), accountDto.getAccountName());
      assertEquals(account.getBalance(), accountDto.getBalance());
      assertEquals(account.getBalanceTarget(), accountDto.getBalanceTarget());
      assertEquals(account.getAccountCategory(), accountDto.getAccountCategory());
      assertEquals(account.getAccountDescription(), accountDto.getAccountDescription());
      assertEquals(account.isDefault(), accountDto.isDefault());
    }
  }

  @Test
  void testToAccount_PartialUpdate() {
    Account account = createAccount();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(25),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(25),
            RandomUtils.randomBoolean());

    Account result = AccountMapper.partialUpdate(account, request);

    assertEquals(request.accountName(), result.getAccountName());
    assertEquals(request.balance(), result.getBalance());
    assertEquals(request.balanceTarget(), result.getBalanceTarget());
    assertEquals(request.accountCategory(), result.getAccountCategory());
    assertEquals(request.accountDescription(), result.getAccountDescription());
    assertEquals(request.isDefault(), result.isDefault());
  }

  private Account createAccount() {
    Account account = new Account();
    account.setAccountId(RandomUtils.randomUUID());
    account.setUsername(RandomUtils.randomString(25));
    account.setAccountName(RandomUtils.randomString(25));
    account.setBalance(RandomUtils.randomBigDecimal());
    account.setBalanceTarget(RandomUtils.randomBigDecimal());
    account.setAccountCategory(RandomUtils.randomEnum(AccountCategoryEnum.class));
    account.setAccountDescription(RandomUtils.randomString(25));
    account.setDefault(RandomUtils.randomBoolean());
    return account;
  }
}
