package it.moneyverse.test.model;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.dto.UserCredential;
import it.moneyverse.test.utils.RandomUtils;

import java.util.List;
import java.util.Optional;

public interface TestContextModel {

  interface Builder {

    Builder withUsers(List<UserModel> users);

    Builder withAccounts(List<AccountModel> accounts);

    TestContextModel build();
  }

  List<UserModel> getUsers();

  List<AccountModel> getAccounts();

}
