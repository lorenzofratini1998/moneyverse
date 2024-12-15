package it.moneyverse.budget.utils.mapper;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;

public class BudgetMapper {

  public static Budget toBudget(BudgetRequestDto request) {
    if (request == null) {
      return null;
    }
    Budget budget = new Budget();
    budget.setUsername(request.username());
    budget.setBudgetName(request.budgetName());
    budget.setDescription(request.description());
    budget.setBudgetLimit(request.budgetLimit());
    budget.setAmount(request.amount());
    return budget;
  }

  public static BudgetDto toBudgetDto(Budget budget) {
    if (budget == null) {
      return null;
    }
    return BudgetDto.builder()
        .withBudgetId(budget.getBudgetId())
        .withUsername(budget.getUsername())
        .withBudgetName(budget.getBudgetName())
        .withDescription(budget.getDescription())
        .withBudgetLimit(budget.getBudgetLimit())
        .withAmount(budget.getAmount())
        .build();
  }

  private BudgetMapper() {}
}
