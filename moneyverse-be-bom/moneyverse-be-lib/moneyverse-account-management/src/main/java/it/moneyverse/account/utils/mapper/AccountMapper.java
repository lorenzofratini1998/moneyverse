package it.moneyverse.account.utils.mapper;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;

public class AccountMapper {

  public static Account toAccount(AccountRequestDto request) {
    if (request == null) {
      return null;
    }
    Account account = new Account();
    account.setUserId(request.userId());
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
        .withUserId(account.getUserId())
        .withAccountName(account.getAccountName())
        .withBalance(account.getBalance())
        .withBalanceTarget(account.getBalanceTarget())
        .withAccountCategory(account.getAccountCategory())
        .withAccountDescription(account.getAccountDescription())
        .withDefault(account.isDefault())
        .build();
  }

  private AccountMapper() {
  }

}
