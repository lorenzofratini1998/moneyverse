package it.moneyverse.account.utils.helper;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;

public class AccountTestHelper {

  public static AccountRequestDto toAccountRequest(Account account) {
    return new AccountRequestDto(
        account.getUsername(),
        account.getAccountName(),
        account.getBalance(),
        account.getBalanceTarget(),
        account.getAccountCategory(),
        account.getAccountDescription(),
        account.isDefault());
  }

  public static AccountDto toAccountDto(AccountRequestDto request) {
    return AccountDto.builder()
        .withUsername(request.username())
        .withAccountName(request.accountName())
        .withAccountCategory(request.accountCategory())
        .withAccountDescription(request.accountDescription())
        .withBalance(request.balance())
        .withBalanceTarget(request.balanceTarget())
        .withDefault(request.isDefault())
        .build();
  }

  private AccountTestHelper() {}
}
