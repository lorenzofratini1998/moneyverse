package it.moneyverse.account.model;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.User;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.utils.helper.MapperTestHelper;

import java.util.List;

public class AccountTestContext implements TestContextModel {

    private final List<AccountModel> accounts;
    private final List<UserModel> users;

    public AccountTestContext(TestContextModel testContext) {
        this.accounts = testContext.getAccounts().stream().map(fakeAccount -> MapperTestHelper.map(fakeAccount, Account.class)).map(AccountModel.class::cast).toList();
        this.users = testContext.getUsers().stream().map(fakeUser -> MapperTestHelper.map(fakeUser, User.class)).map(UserModel.class::cast).toList();
    }

    @Override
    public List<UserModel> getUsers() {
        return users;
    }

    @Override
    public List<AccountModel> getAccounts() {
        return accounts;
    }
}
