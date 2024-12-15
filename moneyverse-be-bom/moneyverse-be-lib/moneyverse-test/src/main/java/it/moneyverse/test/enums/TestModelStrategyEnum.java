package it.moneyverse.test.enums;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.RandomTestContextModel;
import it.moneyverse.test.model.TestContextModel.Builder;
import java.util.List;
import java.util.function.Supplier;

public enum TestModelStrategyEnum {
  RANDOM(
      RandomTestContextModel::builder,
      RandomTestContextModel::createUsers,
      RandomTestContextModel::createAccounts,
      RandomTestContextModel::createBudgets);

  private final Supplier<Builder> builderSupplier;
  private final UserCreationStrategy userCreator;
  private final AccountCreationStrategy accountCreator;
  private final BudgetCreationStrategy budgetCreator;

  TestModelStrategyEnum(
      Supplier<Builder> builderSupplier,
      UserCreationStrategy userCreator,
      AccountCreationStrategy accountCreator,
      BudgetCreationStrategy budgetCreator) {
    this.builderSupplier = builderSupplier;
    this.userCreator = userCreator;
    this.accountCreator = accountCreator;
    this.budgetCreator = budgetCreator;
  }

  public Builder getBuilder() {
    return builderSupplier.get();
  }

  public List<UserModel> createUsers() {
    return userCreator.create();
  }

  public List<AccountModel> createAccounts(List<UserModel> users) {
    return accountCreator.create(users);
  }

  public List<BudgetModel> createBudgets(List<UserModel> users) {
    return budgetCreator.create(users);
  }

  @FunctionalInterface
  interface UserCreationStrategy {

    List<UserModel> create();
  }

  @FunctionalInterface
  interface AccountCreationStrategy {

    List<AccountModel> create(List<UserModel> users);
  }

  @FunctionalInterface
  interface BudgetCreationStrategy {
    List<BudgetModel> create(List<UserModel> users);
  }
}
