package it.moneyverse.account.utils;

import it.moneyverse.account.model.AccountTestContext;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.utils.helper.AccountTestHelper;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.utils.IntegrationTest;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import it.moneyverse.test.utils.helper.ScriptHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.UUID;

public abstract class AccountIntegrationTest extends IntegrationTest {

    protected TestContextModel testContext;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected AccountRepository accountRepository;

    public AccountIntegrationTest() {
        super();
        testContext = new AccountTestContext(super.testContext);
        ScriptHelper.saveScriptFile(tempDir, testContext.getAccounts(), Account.class);
    }

    protected AccountRequestDto createAccountForUser(UUID userId) {
        return AccountTestHelper.toAccountRequest(MapperTestHelper.map(new FakeAccount(userId, testContext.getAccounts().size()), Account.class));
    }
}
