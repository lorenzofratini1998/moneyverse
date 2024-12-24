package it.moneyverse.account.model.entities;

import static it.moneyverse.test.utils.FakeUtils.FAKE_USER;

import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountFactory.class);
  private static final Integer MIN_ACCOUNTS_PER_USER = 5;
  private static final Integer MAX_ACCOUNTS_PER_USER = 20;

  public static List<Account> createAccounts(List<UserModel> users) {
    List<Account> accounts = new ArrayList<>();
    for (UserModel user : users) {
      int numAccountsPerUser =
          RandomUtils.randomInteger(MIN_ACCOUNTS_PER_USER, MAX_ACCOUNTS_PER_USER);
      for (int i = 0; i < numAccountsPerUser; i++) {
        accounts.add(AccountFactory.fakeAccount(user.getUsername(), i));
      }
    }
    LOGGER.info("Created {} random accounts for testing", accounts.size());
    return accounts;
  }

  public static Account fakeAccount(String username, Integer counter) {
    counter = counter + 1;
    Account account = new Account();
    account.setAccountId(RandomUtils.randomUUID());
    account.setUsername(username);
    account.setAccountName("Account %s".formatted(counter));
    account.setBalance(
        RandomUtils.randomDecimal(0.0, Math.random() * 1000).setScale(2, RoundingMode.HALF_EVEN));
    account.setBalanceTarget(
        (int) (Math.random() * 100) % 2 == 0
            ? RandomUtils.randomDecimal(0.0, Math.random() * 2000)
                .setScale(2, RoundingMode.HALF_EVEN)
            : null);
    account.setAccountCategory(RandomUtils.randomEnum(AccountCategoryEnum.class));
    account.setAccountDescription("Account Description %s".formatted(counter));
    account.setDefault(counter == 1);
    account.setCreatedBy(FAKE_USER);
    account.setCreatedAt(LocalDateTime.now());
    account.setUpdatedBy(FAKE_USER);
    account.setUpdatedAt(LocalDateTime.now());
    return account;
  }
}
