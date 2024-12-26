package it.moneyverse.account.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.account.model.dto.AccountCategoryDto;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit test for {@link AccountMapper} */
class AccountMapperTest {

  @Test
  void testToAccountCategoryDto_NullAccountCategory() {
    assertNull(AccountMapper.toAccountCategoryDto(null));
  }

  @Test
  void testToAccountCategoryDto_ValidAccountCategory() {
    AccountCategory category = new AccountCategory();
    category.setAccountCategoryId(RandomUtils.randomBigDecimal().longValue());
    category.setName(RandomUtils.randomString(15).toUpperCase());
    category.setDescription(RandomUtils.randomString(30));

    AccountCategoryDto result = AccountMapper.toAccountCategoryDto(category);

    assertEquals(category.getAccountCategoryId(), result.getAccountCategoryId());
    assertEquals(category.getName(), result.getName());
    assertEquals(category.getDescription(), result.getDescription());
  }

  @Test
  void testToAccountEntity_NullAccountRequest() {
    assertNull(AccountMapper.toAccount(null, null));
  }

  @Test
  void testToAccountEntity_ValidAccountRequest() {
    AccountCategory category = new AccountCategory();
    category.setName(RandomUtils.randomString(15).toUpperCase());
    AccountRequestDto request =
        new AccountRequestDto(
            RandomUtils.randomString(25),
            RandomUtils.randomString(25),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            category.getName(),
            RandomUtils.randomString(25),
            RandomUtils.randomEnum(CurrencyEnum.class));

    Account account = AccountMapper.toAccount(request, category);

    assertEquals(request.username(), account.getUsername());
    assertEquals(request.accountName(), account.getAccountName());
    assertEquals(request.balance(), account.getBalance());
    assertEquals(request.balanceTarget(), account.getBalanceTarget());
    assertEquals(request.accountCategory(), account.getAccountCategory().getName());
    assertEquals(request.accountDescription(), account.getAccountDescription());
    assertEquals(request.currency(), account.getCurrency());
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
    assertEquals(account.getAccountCategory().getName(), accountDto.getAccountCategory());
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
      assertEquals(account.getAccountCategory().getName(), accountDto.getAccountCategory());
      assertEquals(account.getAccountDescription(), accountDto.getAccountDescription());
      assertEquals(account.isDefault(), accountDto.isDefault());
    }
  }

  @Test
  void testToAccount_PartialUpdate() {
    Account account = createAccount();
    AccountCategory category = new AccountCategory();
    category.setName(RandomUtils.randomString(15).toUpperCase());
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(25),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            category.getName(),
            RandomUtils.randomString(25),
            RandomUtils.randomEnum(CurrencyEnum.class),
            RandomUtils.randomBoolean());

    Account result = AccountMapper.partialUpdate(account, request, category);

    assertEquals(request.accountName(), result.getAccountName());
    assertEquals(request.balance(), result.getBalance());
    assertEquals(request.balanceTarget(), result.getBalanceTarget());
    assertEquals(request.accountCategory(), result.getAccountCategory().getName());
    assertEquals(request.accountDescription(), result.getAccountDescription());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(request.isDefault(), result.isDefault());
  }

  private Account createAccount() {
    Account account = new Account();
    AccountCategory category = new AccountCategory();
    category.setName(RandomUtils.randomString(15).toUpperCase());
    account.setAccountId(RandomUtils.randomUUID());
    account.setUsername(RandomUtils.randomString(25));
    account.setAccountName(RandomUtils.randomString(25));
    account.setBalance(RandomUtils.randomBigDecimal());
    account.setBalanceTarget(RandomUtils.randomBigDecimal());
    account.setAccountCategory(category);
    account.setAccountDescription(RandomUtils.randomString(25));
    account.setDefault(RandomUtils.randomBoolean());
    return account;
  }
}
