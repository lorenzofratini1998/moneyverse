package it.moneyverse.account.utils.mapper;

import it.moneyverse.account.model.dto.AccountCategoryDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.model.dto.AccountDto;
import java.util.Collections;
import java.util.List;

public class AccountMapper {

  private AccountMapper() {}

  public static AccountCategoryDto toAccountCategoryDto(AccountCategory accountCategory) {
    if (accountCategory == null) {
      return null;
    }
    return AccountCategoryDto.builder()
        .withAccountCategoryId(accountCategory.getAccountCategoryId())
        .withName(accountCategory.getName().toUpperCase())
        .withDescription(accountCategory.getDescription())
        .build();
  }

  public static Account toAccount(AccountRequestDto request, AccountCategory accountCategory) {
    if (request == null) {
      return null;
    }
    Account account = new Account();
    account.setUserId(request.userId());
    account.setAccountName(request.accountName());
    account.setBalance(request.balance());
    account.setBalanceTarget(request.balanceTarget());
    account.setAccountCategory(accountCategory);
    account.setAccountDescription(request.accountDescription());
    account.setCurrency(request.currency());
    return account;
  }

  public static AccountDto toAccountDto(Account account) {
    if (account == null) {
      return null;
    }
    return AccountDto.builder()
        .withAccountId(account.getAccountId())
        .withUserId(account.getUserId())
        .withAccountName(account.getAccountName())
        .withBalance(account.getBalance())
        .withBalanceTarget(account.getBalanceTarget())
        .withAccountCategory(account.getAccountCategory().getName().toUpperCase())
        .withAccountDescription(account.getAccountDescription())
        .withCurrency(account.getCurrency())
        .withDefault(account.isDefault())
        .build();
  }

  public static List<AccountDto> toAccountDto(List<Account> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(AccountMapper::toAccountDto).toList();
  }

  public static Account partialUpdate(
      Account account, AccountUpdateRequestDto request, AccountCategory accountCategory) {
    if (request == null) {
      return null;
    }
    if (request.accountName() != null) {
      account.setAccountName(request.accountName());
    }
    if (request.balance() != null) {
      account.setBalance(request.balance());
    }
    if (request.balanceTarget() != null) {
      account.setBalanceTarget(request.balanceTarget());
    }
    if (request.accountCategory() != null) {
      account.setAccountCategory(accountCategory);
    }
    if (request.accountDescription() != null) {
      account.setAccountDescription(request.accountDescription());
    }
    if (request.isDefault() != null) {
      account.setDefault(request.isDefault());
    }
    return account;
  }
}
