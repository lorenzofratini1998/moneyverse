package it.moneyverse.account.utils;

import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.utils.helper.AccountTestHelper;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.utils.IntegrationTest;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import java.util.function.Function;
import java.util.stream.Stream;

import java.util.UUID;

public abstract class AccountIntegrationTest extends IntegrationTest {

    protected AccountRequestDto createAccountForUser(UUID userId) {
        return AccountTestHelper.toAccountRequest(MapperTestHelper.map(new FakeAccount(userId, testModel.getAccounts().size()), Account.class));
    }

    protected static Stream<Function<UUID, AccountRequestDto>> invalidAccountRequestProvider() {
        return Stream.of(
            AccountIntegrationTest::createAccountWithNullUserId,
            AccountIntegrationTest::createAccountWithNullAccountName,
            AccountIntegrationTest::createAccountWithNullAccountCategory);
    }

    protected static AccountRequestDto createAccountWithNullUserId(UUID userId) {
        Account account = MapperTestHelper.map(new FakeAccount(userId, testModel.getAccounts().size()), Account.class);
        account.setUserId(null);
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountWithNullAccountName(UUID userId) {
        Account account = MapperTestHelper.map(new FakeAccount(userId, testModel.getAccounts().size()), Account.class);
        account.setAccountName(null);
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountRequestForExistentAccount(UUID userId) {
        AccountModel randomExistingAccount = testModel.getRandomAccount(userId);
        Account account = MapperTestHelper.map(new FakeAccount(userId, testModel.getAccounts().size()), Account.class);
        account.setAccountName(randomExistingAccount.getAccountName());
        return AccountTestHelper.toAccountRequest(account);
    }

    protected static AccountRequestDto createAccountWithNullAccountCategory(UUID userId) {
        Account account = MapperTestHelper.map(new FakeAccount(userId, testModel.getAccounts().size()), Account.class);
        account.setAccountCategory(null);
        return AccountTestHelper.toAccountRequest(account);
    }
}
