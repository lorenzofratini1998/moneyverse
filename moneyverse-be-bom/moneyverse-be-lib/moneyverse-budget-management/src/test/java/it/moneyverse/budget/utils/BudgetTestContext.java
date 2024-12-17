package it.moneyverse.budget.utils;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.entities.FakeBudget;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import org.springframework.data.domain.Sort;

import java.util.List;

import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.*;

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

  public List<BudgetModel> filterBudgets(BudgetCriteria criteria) {
    return model.getBudgets().stream()
            .filter(budget -> criteria
                    .getUsername()
                    .map(username -> username.equals(budget.getUsername()))
                    .orElse(true))
            .filter(budget -> criteria
                    .getAmount()
                    .map(
                            amountCriteria ->
                                    budget.getAmount() != null && filterByBound(budget.getAmount(), amountCriteria))
                            .orElse(true))
            .filter(budget -> criteria
                    .getBudgetLimit()
                    .map(
                            budgetLimitCriteria ->
                                    budget.getBudgetLimit() != null && filterByBound(budget.getBudgetLimit(), budgetLimitCriteria))
                            .orElse(true))
            .sorted((a, b) -> sortByCriteria(a,b, criteria.getSort()))
            .skip(criteria.getPage().getOffset())
            .limit(criteria.getPage().getLimit())
            .toList();
  }

  private int sortByCriteria(
          BudgetModel a, BudgetModel b, SortCriteria<BudgetSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return 0;
    }

    SortAttribute attribute = sortCriteria.getAttribute();
    Sort.Direction direction = sortCriteria.getDirection();

    int comparison =
            switch (attribute) {
              case BUDGET_NAME -> a.getBudgetName().compareTo(b.getBudgetName());
              case AMOUNT -> a.getAmount().compareTo(b.getAmount());
              case BUDGET_LIMIT -> a.getBudgetLimit().compareTo(b.getBudgetLimit());
              default -> 0;
            };

    return direction == Sort.Direction.ASC ? comparison : -comparison;
  }

  public BudgetCriteria createBudgetCriteria() {
    return new BudgetCriteriaRandomGenerator(getCurrentInstance()).generate();
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
