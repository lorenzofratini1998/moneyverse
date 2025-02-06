package it.moneyverse.budget.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;
import static it.moneyverse.test.utils.FakeUtils.FAKE_USER;

import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BudgetFactory {

  public static List<Budget> createBudgets(List<Category> categories) {
    List<Budget> budgets = new ArrayList<>();
    for (Category category : categories) {
      for (int i = 0;
          i < RandomUtils.randomInteger(MIN_BUDGETS_PER_CATEGORY, MAX_BUDGETS_PER_CATEGORY);
          i++) {
        budgets.add(createBudget(category, budgets));
      }
    }
    return budgets;
  }

  private static Budget createBudget(Category category, List<Budget> budgets) {
    Budget fakeBudget = fakeBudget(category);
    if (budgets.stream()
        .anyMatch(
            b ->
                b.getCategory().getCategoryId().equals(fakeBudget.getCategory().getCategoryId())
                    && b.getStartDate().equals(fakeBudget.getStartDate())
                    && b.getEndDate().equals(fakeBudget.getEndDate()))) {
      return createBudget(category, budgets);
    }
    return fakeBudget;
  }

  private static Budget fakeBudget(Category category) {
    Budget budget = new Budget();
    budget.setBudgetId(RandomUtils.randomUUID());
    budget.setCategory(category);
    budget.setStartDate(RandomUtils.randomLocalDate(2025, 2025));
    budget.setEndDate(budget.getStartDate().plusMonths(RandomUtils.randomInteger(1, 3)));
    budget.setAmount(RandomUtils.randomBigDecimal());
    budget.setBudgetLimit(RandomUtils.randomBigDecimal());
    budget.setCurrency(RandomUtils.randomString(3));
    budget.setCreatedBy(FAKE_USER);
    budget.setCreatedAt(LocalDateTime.now());
    budget.setUpdatedBy(FAKE_USER);
    budget.setUpdatedAt(LocalDateTime.now());
    return budget;
  }

  private BudgetFactory() {}
}
