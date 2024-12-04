package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;

import java.util.List;

public interface TestContextModel {

  interface Builder {

    Builder withUsers(List<UserModel> users);

    Builder withAccounts(List<AccountModel> accounts);

    TestContextModel build();
  }

  List<UserModel> getUsers();

  List<AccountModel> getAccounts();

}
