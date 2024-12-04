package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import java.util.List;
import java.util.Objects;

public class TestModelBuilder {

  private final TestModelStrategyEnum strategy;

  public TestModelBuilder(TestModelStrategyEnum strategy) {
    this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
  }

  public TestContextModel buildTestModel(boolean withTestUsers, boolean withTestAccounts) {
    TestContextModel.Builder modelBuilder = strategy.getBuilder();
    if (withTestUsers) {
      List<UserModel> users = strategy.createUsers();
      modelBuilder = modelBuilder.withUsers(users);
      if (withTestAccounts) {
        modelBuilder = modelBuilder.withAccounts(strategy.createAccounts(users));
      }
    }
    return modelBuilder.build();
  }
}
