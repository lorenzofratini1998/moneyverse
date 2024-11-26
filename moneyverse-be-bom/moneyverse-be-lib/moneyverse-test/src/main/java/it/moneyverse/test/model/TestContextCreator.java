package it.moneyverse.test.model;

import it.moneyverse.model.entities.Account;
import it.moneyverse.model.entities.AccountModel;
import it.moneyverse.model.entities.User;
import it.moneyverse.model.entities.UserModel;
import java.util.List;

public interface TestContextCreator {

  List<UserModel> createUsers();

  List<AccountModel> createAccounts(List<UserModel> users);
}
