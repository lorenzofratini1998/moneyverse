package it.moneyverse.account.utils.mapper;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
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
    account.setDefault(request.isDefault());
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
    return entities.stream().map(AccountMapper::toAccountDto).collect(Collectors.toList());
  }
}
