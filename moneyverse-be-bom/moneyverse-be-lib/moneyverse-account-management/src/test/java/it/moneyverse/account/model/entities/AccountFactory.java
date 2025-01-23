package it.moneyverse.account.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.enums.CurrencyEnum;
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

  public static List<AccountCategory> createAccountCategories() {
    List<AccountCategory> accountCategories = new ArrayList<>();
    for (int i = 0; i < ACCOUNT_CATEGORY_NUMBER; i++) {
      accountCategories.add(AccountFactory.fakeAccountCategory(i));
    }
    return accountCategories;
  }

  public static AccountCategory fakeAccountCategory(int counter) {
    counter = counter + 1;
    AccountCategory category = new AccountCategory();
    category.setAccountCategoryId((long) counter);
    category.setName("CATEGORY %s".formatted(counter));
    category.setDescription(RandomUtils.randomString(20));
    return category;
  }

  public static List<Account> createAccounts(
      List<UserModel> users, List<AccountCategory> categories) {
    List<Account> accounts = new ArrayList<>();
    for (UserModel user : users) {
      int numAccountsPerUser =
          RandomUtils.randomInteger(MIN_ACCOUNTS_PER_USER, MAX_ACCOUNTS_PER_USER);
      for (int i = 0; i < numAccountsPerUser; i++) {
        AccountCategory category =
            categories.get(RandomUtils.randomInteger(0, categories.size() - 1));
        accounts.add(AccountFactory.fakeAccount(user.getUsername(), category, i));
      }
    }
    LOGGER.info("Created {} random accounts for testing", accounts.size());
    return accounts;
  }

  public static Account fakeAccount(String username, AccountCategory category, Integer counter) {
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
    account.setAccountCategory(category);
    account.setAccountDescription("Account Description %s".formatted(counter));
    account.setCurrency(RandomUtils.randomEnum(CurrencyEnum.class));
    account.setDefault(counter == 1);
    account.setCreatedBy(FAKE_USER);
    account.setCreatedAt(LocalDateTime.now());
    account.setUpdatedBy(FAKE_USER);
    account.setUpdatedAt(LocalDateTime.now());
    return account;
  }
}
