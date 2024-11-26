package it.moneyverse.test.model;

import it.moneyverse.model.entities.Account;
import it.moneyverse.model.entities.AccountModel;
import it.moneyverse.model.entities.User;
import it.moneyverse.model.entities.UserModel;
import java.util.List;

public class TestContext {

  private final List<UserModel> users;
  private final List<AccountModel> accounts;

  public TestContext(TestContextCreator creator) {
    this.users = creator.createUsers();
    this.accounts = creator.createAccounts(users);
  }

  public List<UserModel> getUsers() {
    return users;
  }

  public List<AccountModel> getAccounts() {
    return accounts;
  }

}
