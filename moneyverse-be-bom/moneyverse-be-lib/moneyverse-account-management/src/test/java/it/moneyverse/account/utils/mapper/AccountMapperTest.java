package it.moneyverse.account.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.account.model.AccountTestFactory;
import it.moneyverse.account.model.dto.AccountCategoryDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
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
    AccountCategory category = AccountTestFactory.fakeAccountCategory();

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
        AccountTestFactory.AccountRequestDtoBuilder.builder()
            .withAccountCategory(category.getName())
            .build();

    Account account = AccountMapper.toAccount(request, category);

    assertEquals(request.userId(), account.getUserId());
    assertEquals(request.accountName(), account.getAccountName());
    assertEquals(
        request.balance() != null ? request.balance() : BigDecimal.ZERO, account.getBalance());
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
    Account account = AccountTestFactory.fakeAccount();

    AccountDto accountDto = AccountMapper.toAccountDto(account);

    assertEquals(account.getAccountId(), accountDto.getAccountId());
    assertEquals(account.getUserId(), accountDto.getUserId());
    assertEquals(account.getAccountName(), accountDto.getAccountName());
    assertEquals(account.getBalance(), accountDto.getBalance());
    assertEquals(account.getBalanceTarget(), accountDto.getBalanceTarget());
    assertEquals(account.getCurrency(), accountDto.getCurrency());
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
      accounts.add(AccountTestFactory.fakeAccount());
    }

    List<AccountDto> accountDtos = AccountMapper.toAccountDto(accounts);

    for (int i = 0; i < entitiesCount; i++) {
      Account account = accounts.get(i);
      AccountDto accountDto = accountDtos.get(i);

      assertEquals(account.getAccountId(), accountDto.getAccountId());
      assertEquals(account.getUserId(), accountDto.getUserId());
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
    Account account = AccountTestFactory.fakeAccount();
    AccountCategory category = new AccountCategory();
    category.setName(RandomUtils.randomString(15).toUpperCase());
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(25),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            category.getName(),
            RandomUtils.randomString(25),
            RandomUtils.randomBoolean());

    Account result = AccountMapper.partialUpdate(account, request, category);

    assertEquals(request.accountName(), result.getAccountName());
    assertEquals(request.balance(), result.getBalance());
    assertEquals(request.balanceTarget(), result.getBalanceTarget());
    assertEquals(request.accountCategory(), result.getAccountCategory().getName());
    assertEquals(request.accountDescription(), result.getAccountDescription());
    assertEquals(request.isDefault(), result.isDefault());
  }
}
