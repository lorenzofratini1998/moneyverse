package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import java.util.List;
import java.util.UUID;

public class TestContext implements TestContextModel {

  private final List<UserModel> users;
  private final List<AccountModel> accounts;

  public TestContext(TestContextCreator creator) {
    this.users = creator.createUsers();
    this.accounts = creator.createAccounts(users);
  }

  @Override
  public List<UserModel> getUsers() {
    return users;
  }

  @Override
  public List<AccountModel> getAccounts() {
    return accounts;
  }

  public List<AccountModel> getAccountsByUser(UUID userId) {
    return accounts.stream().filter(account -> account.getUserId().equals(userId)).toList();
  }

}
