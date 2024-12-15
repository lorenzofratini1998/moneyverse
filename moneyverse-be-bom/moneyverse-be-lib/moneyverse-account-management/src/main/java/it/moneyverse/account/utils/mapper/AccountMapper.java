package it.moneyverse.account.utils.mapper;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

  private AccountMapper() {}

  public static Account toAccount(AccountRequestDto request) {
    if (request == null) {
      return null;
    }
    Account account = new Account();
    account.setUsername(request.username());
    account.setAccountName(request.accountName());
    account.setBalance(request.balance());
    account.setBalanceTarget(request.balanceTarget());
    account.setAccountCategory(request.accountCategory());
    account.setAccountDescription(request.accountDescription());
    return account;
  }

  public static AccountDto toAccountDto(Account account) {
    if (account == null) {
      return null;
    }
    return AccountDto.builder()
        .withAccountId(account.getAccountId())
        .withUsername(account.getUsername())
        .withAccountName(account.getAccountName())
        .withBalance(account.getBalance())
        .withBalanceTarget(account.getBalanceTarget())
        .withAccountCategory(account.getAccountCategory())
        .withAccountDescription(account.getAccountDescription())
        .withDefault(account.isDefault())
        .build();
  }

  public static List<AccountDto> toAccountDto(List<Account> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(AccountMapper::toAccountDto).toList();
  }

  public static Account partialUpdate(Account account, AccountUpdateRequestDto request) {
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
      account.setAccountCategory(request.accountCategory());
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
