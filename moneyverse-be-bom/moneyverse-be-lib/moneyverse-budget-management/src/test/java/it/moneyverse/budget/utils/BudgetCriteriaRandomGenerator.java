package it.moneyverse.budget.utils;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.CriteriaRandomGenerator;
import it.moneyverse.test.utils.RandomUtils;
import org.springframework.data.domain.Sort;

public class BudgetCriteriaRandomGenerator extends CriteriaRandomGenerator<BudgetCriteria> {

  private final BudgetCriteria criteria;
  private final BudgetTestContext testContext;

  public BudgetCriteriaRandomGenerator(BudgetTestContext testContext) {
    this.testContext = testContext;
    this.criteria = new BudgetCriteria();
  }

  @Override
  public BudgetCriteria generate() {
    withRandomUsername();
    withRandomAmount();
    withRandomBudgetLimit();
    withRandomCurrency();
    withPage();
    withSort();
    return criteria;
  }

  private void withRandomUsername() {
    criteria.setUsername(Math.random() < 0.5 ? testContext.getRandomUser().getUsername() : null);
  }

  private void withRandomAmount() {
    criteria.setAmount(
        Math.random() < 0.5
            ? randomCriteriaBound(testContext.getBudgets().stream().map(Budget::getAmount).toList())
            : null);
  }

  private void withRandomBudgetLimit() {
    criteria.setBudgetLimit(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getBudgets().stream().map(Budget::getBudgetLimit).toList())
            : null);
  }

  private void withRandomCurrency() {
    criteria.setCurrency(Math.random() < 0.5 ? RandomUtils.randomString(3).toUpperCase() : null);
  }

  private void withPage() {
    criteria.setPage(new PageCriteria());
  }

  private void withSort() {
    criteria.setSort(new SortCriteria<>(BudgetSortAttributeEnum.BUDGET_NAME, Sort.Direction.ASC));
  }
}
