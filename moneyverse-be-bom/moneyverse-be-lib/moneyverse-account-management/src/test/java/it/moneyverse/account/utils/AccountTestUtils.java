package it.moneyverse.account.utils;

import it.moneyverse.model.entities.Account;
import it.moneyverse.model.entities.AccountModel;
import java.util.List;

public class AccountTestUtils {

  public static List<Account> toAccount(List<AccountModel> accounts) {
    return accounts.stream().map(fakeAccount -> {
      Account account = new Account();
      account.setAccountId(fakeAccount.getAccountId());
      account.setUserId(fakeAccount.getUserId());
      account.setAccountName(fakeAccount.getAccountName());
      account.setBalance(fakeAccount.getBalance());
      account.setAccountCategory(fakeAccount.getAccountCategory());
      account.setAccountDescription(fakeAccount.getAccountDescription());
      account.setDefault(fakeAccount.isDefault());
      account.setCreatedBy(fakeAccount.getCreatedBy());
      account.setCreatedAt(fakeAccount.getCreatedAt());
      account.setUpdatedBy(fakeAccount.getUpdatedBy());
      account.setUpdatedAt(fakeAccount.getUpdatedAt());
      return account;
    }).toList();
  }

}
