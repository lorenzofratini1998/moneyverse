package it.moneyverse.budget.utils;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.model.entities.FakeBudget;
import it.moneyverse.test.utils.helper.MapperTestHelper;

import java.util.List;

public class BudgetTestContext extends TestContext {

  protected BudgetTestContext(Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public BudgetRequestDto createBudgetForUser(String username) {
    return toBudgetRequest(
        MapperTestHelper.map(new FakeBudget(username, model.getBudgets().size()), Budget.class));
  }

  private BudgetRequestDto toBudgetRequest(Budget budget) {
    return new BudgetRequestDto(
        budget.getUsername(),
        budget.getBudgetName(),
        budget.getDescription(),
        budget.getBudgetLimit(),
        budget.getAmount());
  }

  public BudgetDto getExpectedBudgetDto(BudgetRequestDto request) {
    return BudgetDto.builder()
        .withUsername(request.username())
        .withBudgetName(request.budgetName())
        .withDescription(request.description())
        .withBudgetLimit(request.budgetLimit())
        .withAmount(request.amount())
        .build();
  }

  public int getBudgetsCount() {
    return model.getBudgets().size();
  }

  public static class Builder extends TestContext.Builder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public BudgetTestContext build() {
      return new BudgetTestContext(this);
    }
  }
}
