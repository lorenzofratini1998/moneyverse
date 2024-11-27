package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;

import java.util.List;

public interface TestContextModel {

    List<UserModel> getUsers();

    List<AccountModel> getAccounts();

    default UserModel getRandomUser() {
        return getUsers().get(RandomUtils.randomInteger(0, getUsers().size() - 1));
    }
}
