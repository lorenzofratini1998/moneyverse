package it.moneyverse.test.enums;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.RandomTestContextModel;
import it.moneyverse.test.model.TestContextModel.Builder;
import java.util.List;
import java.util.function.Supplier;

public enum TestModelStrategyEnum {
  RANDOM(
      RandomTestContextModel::builder,
      RandomTestContextModel::createUsers,
      RandomTestContextModel::createAccounts
  );

  private final Supplier<Builder> builderSupplier;
  private final UserCreationStrategy userCreator;
  private final AccountCreationStrategy accountCreator;

  TestModelStrategyEnum(
      Supplier<Builder> builderSupplier,
      UserCreationStrategy userCreator,
      AccountCreationStrategy accountCreator
  ) {
    this.builderSupplier = builderSupplier;
    this.userCreator = userCreator;
    this.accountCreator = accountCreator;
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

  @FunctionalInterface
  interface UserCreationStrategy {

    List<UserModel> create();
  }

  @FunctionalInterface
  interface AccountCreationStrategy {

    List<AccountModel> create(List<UserModel> users);
  }
}
