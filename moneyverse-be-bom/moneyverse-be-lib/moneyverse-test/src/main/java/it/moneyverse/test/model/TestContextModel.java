package it.moneyverse.test.model;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.dto.UserCredential;
import it.moneyverse.test.utils.RandomUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestContextModel {

  interface Builder {

    Builder withUsers(List<UserModel> users);

    Builder withAccounts(List<AccountModel> accounts);

    TestContextModel build();
  }

  List<UserModel> getUsers();

  List<AccountModel> getAccounts();

  default UserModel getRandomUser() {
    return getUsers().get(RandomUtils.randomInteger(0, getUsers().size() - 1));
  }

  default UserModel getUser(String username) {
    return getUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  default UserCredential getUserCredential(String username) {
    return Optional.of(getUser(username))
        .map(user -> new UserCredential(user.getUsername(), user.getPassword()))
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  default UserModel getAdminUser() {
    return getUsers().stream().filter(user -> user.getRole().equals(UserRoleEnum.ADMIN)).findFirst()
        .orElseThrow(() -> new IllegalStateException("No admin found"));
  }

    default AccountModel getRandomAccount(String username)
    {
        List<AccountModel> userAccounts = getAccounts().stream()
            .filter(account -> account.getUsername().equals(username))
            .toList();
        return userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    }
}
