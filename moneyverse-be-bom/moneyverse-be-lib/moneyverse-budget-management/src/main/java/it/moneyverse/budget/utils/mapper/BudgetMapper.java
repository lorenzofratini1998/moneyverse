package it.moneyverse.budget.utils.mapper;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import java.util.Collections;
import java.util.List;

public class BudgetMapper {

  public static Budget toBudget(BudgetRequestDto request, Category category) {
    if (request == null) {
      return null;
    }
    Budget budget = new Budget();
    budget.setCategory(category);
    budget.setStartDate(request.startDate());
    budget.setEndDate(request.endDate());
    budget.setBudgetLimit(request.budgetLimit());
    budget.setCurrency(request.currency());
    return budget;
  }

  public static List<BudgetDto> toBudgetDto(List<Budget> budgets) {
    if (budgets.isEmpty()) {
      return Collections.emptyList();
    }
    return budgets.stream().map(BudgetMapper::toBudgetDto).toList();
  }

  public static BudgetDto toBudgetDto(Budget budget) {
    if (budget == null) {
      return null;
    }
    return new BudgetDto.Builder()
        .withBudgetId(budget.getBudgetId())
        .withCategory(CategoryMapper.toCategoryDto(budget.getCategory()))
        .withStartDate(budget.getStartDate())
        .withEndDate(budget.getEndDate())
        .withAmount(budget.getAmount())
        .withBudgetLimit(budget.getBudgetLimit())
        .withCurrency(budget.getCurrency())
        .build();
  }

  public static Budget partialUpdate(Budget budget, BudgetUpdateRequestDto request) {
    if (request == null) {
      return null;
    }
    if (request.startDate() != null) {
      budget.setStartDate(request.startDate());
    }
    if (request.endDate() != null) {
      budget.setEndDate(request.endDate());
    }
    if (request.amount() != null) {
      budget.setAmount(request.amount());
    }
    if (request.budgetLimit() != null) {
      budget.setBudgetLimit(request.budgetLimit());
    }
    if (request.currency() != null) {
      budget.setCurrency(request.currency());
    }
    return budget;
  }

  private BudgetMapper() {}
}
