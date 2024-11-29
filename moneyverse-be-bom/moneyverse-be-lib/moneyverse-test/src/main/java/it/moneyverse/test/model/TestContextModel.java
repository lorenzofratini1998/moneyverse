package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;

import java.util.List;
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

    default AccountModel getRandomAccount(UUID userId)
    {
        List<AccountModel> userAccounts = getAccounts().stream()
            .filter(account -> account.getUserId().equals(userId))
            .toList();
        return userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
    }
}
