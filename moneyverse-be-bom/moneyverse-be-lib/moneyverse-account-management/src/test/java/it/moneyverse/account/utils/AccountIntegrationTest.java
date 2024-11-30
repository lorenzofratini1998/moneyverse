package it.moneyverse.account.utils;

import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.utils.helper.AccountTestHelper;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.utils.IntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AccountIntegrationTest extends IntegrationTest {

    protected AccountRequestDto createAccountForUser(String username) {
        return AccountTestHelper.toAccountRequest(MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class));
    }

    protected static Stream<Function<String, AccountRequestDto>> invalidAccountRequestProvider() {
        return Stream.of(
            AccountIntegrationTest::createAccountWithNullUsername,
            AccountIntegrationTest::createAccountWithNullAccountName,
            AccountIntegrationTest::createAccountWithNullAccountCategory,
            AccountIntegrationTest::createAccountWithExceedUsername);
    }

    protected static AccountRequestDto createAccountWithNullUsername(String username) {
        Account account = MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class);
        account.setUsername(null);
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountWithNullAccountName(String username) {
        Account account = MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class);
        account.setAccountName(null);
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountWithNullAccountCategory(String username) {
        Account account = MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class);
        account.setAccountCategory(null);
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountWithExceedUsername(String username) {
        Account account = MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class);
        account.setUsername(RandomUtils.randomString(100));
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createExistentAccountForUser(String username) {
        AccountModel randomExistingAccount = testModel.getRandomAccount(username);
        Account account = MapperTestHelper.map(new FakeAccount(username, testModel.getAccounts().size()), Account.class);
        account.setAccountName(randomExistingAccount.getAccountName());
        return AccountTestHelper.toAccountRequest(account);
    }
}
