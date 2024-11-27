package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import java.util.List;

public interface TestContextCreator {

  List<UserModel> createUsers();

  List<AccountModel> createAccounts(List<UserModel> users);
}
