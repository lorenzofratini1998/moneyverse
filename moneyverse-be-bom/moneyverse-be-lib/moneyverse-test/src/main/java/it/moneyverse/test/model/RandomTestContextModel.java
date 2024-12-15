package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.model.entities.FakeBudget;
import it.moneyverse.test.model.entities.FakeUser;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomTestContextModel implements TestContextModel {

  private static final Integer MIN_USERS = 50;
  private static final Integer MAX_USERS = 200;
  private static final Integer MIN_ACCOUNTS_PER_USER = 5;
  private static final Integer MAX_ACCOUNTS_PER_USER = 20;
  private static final Integer MIN_BUDGETS_PER_USER = 3;
  private static final Integer MAX_BUDGETS_PER_USER = 15;
  private static final Logger LOGGER = LoggerFactory.getLogger(RandomTestContextModel.class);

  private final List<UserModel> users;
  private final List<AccountModel> accounts;
  private final List<BudgetModel> budgets;

  public RandomTestContextModel(Builder builder) {
    this.users = builder.users;
    this.accounts = builder.accounts;
    this.budgets = builder.budgets;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static List<UserModel> createUsers() {
    int numUsers = RandomUtils.randomInteger(MIN_USERS, MAX_USERS);
    List<UserModel> users = new ArrayList<>();
    for (int i = 0; i < numUsers; i++) {
      users.add(new FakeUser(i));
    }
    LOGGER.info("Created {} random users for testing", users.size());
    return users;
  }

  public static List<AccountModel> createAccounts(List<UserModel> users) {
    List<AccountModel> accounts = new ArrayList<>();
    for (UserModel user : users) {
      List<AccountModel> accountsByUser = randomAccounts(user.getUsername());
      accounts.addAll(accountsByUser);
    }
    LOGGER.info("Created {} random accounts for testing", accounts.size());
    return accounts;
  }

  private static List<AccountModel> randomAccounts(String username) {
    int numAccountsPerUser =
        RandomUtils.randomInteger(MIN_ACCOUNTS_PER_USER, MAX_ACCOUNTS_PER_USER);
    List<AccountModel> accounts = new ArrayList<>();
    for (int i = 0; i < numAccountsPerUser; i++) {
      accounts.add(new FakeAccount(username, i));
    }
    return accounts;
  }

  public static List<BudgetModel> createBudgets(List<UserModel> users) {
    List<BudgetModel> budgets = new ArrayList<>();
    for (UserModel user : users) {
      List<BudgetModel> budgetsByUser = randomBudgets(user.getUsername());
      budgets.addAll(budgetsByUser);
    }
    LOGGER.info("Created {} random budgets for testing", budgets.size());
    return budgets;
  }

  private static List<BudgetModel> randomBudgets(String username) {
    int numBudgetsPerUser = RandomUtils.randomInteger(MIN_BUDGETS_PER_USER, MAX_BUDGETS_PER_USER);
    List<BudgetModel> budgets = new ArrayList<>();
    for (int i = 0; i < numBudgetsPerUser; i++) {
      budgets.add(new FakeBudget(username, i));
    }
    return budgets;
  }

  @Override
  public List<UserModel> getUsers() {
    return users;
  }

  @Override
  public List<AccountModel> getAccounts() {
    return accounts;
  }

  @Override
  public List<BudgetModel> getBudgets() {
    return budgets;
  }

  public static class Builder implements TestContextModel.Builder {

    private List<UserModel> users;
    private List<AccountModel> accounts;
    private List<BudgetModel> budgets;

    public Builder withUsers(List<UserModel> users) {
      this.users = users;
      return this;
    }

    public Builder withAccounts(List<AccountModel> accounts) {
      this.accounts = accounts;
      return this;
    }

    @Override
    public TestContextModel.Builder withBudgets(List<BudgetModel> budgets) {
      this.budgets = budgets;
      return this;
    }

    public RandomTestContextModel build() {
      return new RandomTestContextModel(this);
    }
  }
}
