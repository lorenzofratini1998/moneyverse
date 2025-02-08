package it.moneyverse.budget.utils;

import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.DefaultCategory;
import it.moneyverse.test.utils.RandomUtils;
import java.util.UUID;

public class BudgetTestUtils {

  public static BudgetRequestDto createBudgetRequest() {
    return new BudgetRequestDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3));
  }

  public static BudgetRequestDto createBudgetRequest(UUID categoryId) {
    return new BudgetRequestDto(
        categoryId,
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3));
  }

  public static Budget createBudget(Category category) {
    Budget budget = new Budget();
    budget.setBudgetId(RandomUtils.randomUUID());
    budget.setCategory(category);
    budget.setStartDate(RandomUtils.randomLocalDate(2025, 2025));
    budget.setEndDate(RandomUtils.randomLocalDate(2025, 2025));
    budget.setBudgetLimit(RandomUtils.randomBigDecimal());
    budget.setCurrency(RandomUtils.randomString(3));
    return budget;
  }

  public static DefaultCategory createDefaultCategory() {
    DefaultCategory defaultCategory = new DefaultCategory();
    defaultCategory.setId(RandomUtils.randomUUID());
    defaultCategory.setName(RandomUtils.randomString(15));
    defaultCategory.setDescription(RandomUtils.randomString(15));
    return defaultCategory;
  }

  public static BudgetUpdateRequestDto createBudgetUpdateRequest() {
    return new BudgetUpdateRequestDto(
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3));
  }

  private BudgetTestUtils() {}
}
