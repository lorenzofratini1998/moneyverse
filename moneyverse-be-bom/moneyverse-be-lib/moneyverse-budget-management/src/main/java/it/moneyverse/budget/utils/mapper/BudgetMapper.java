package it.moneyverse.budget.utils.mapper;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import java.util.Collections;
import java.util.List;

public class BudgetMapper {

  public static Budget toBudget(BudgetRequestDto request) {
    if (request == null) {
      return null;
    }
    Budget budget = new Budget();
    budget.setUserId(request.userId());
    budget.setBudgetName(request.budgetName());
    budget.setDescription(request.description());
    budget.setBudgetLimit(request.budgetLimit());
    budget.setAmount(request.amount());
    budget.setCurrency(request.currency());
    return budget;
  }

  public static BudgetDto toBudgetDto(Budget budget) {
    if (budget == null) {
      return null;
    }
    return BudgetDto.builder()
        .withBudgetId(budget.getBudgetId())
        .withUserId(budget.getUserId())
        .withBudgetName(budget.getBudgetName())
        .withDescription(budget.getDescription())
        .withBudgetLimit(budget.getBudgetLimit())
        .withAmount(budget.getAmount())
        .withCurrency(budget.getCurrency())
        .build();
  }

  public static List<BudgetDto> toBudgetDto(List<Budget> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(BudgetMapper::toBudgetDto).toList();
  }

  public static Budget partialUpdate(Budget budget, BudgetUpdateRequestDto request) {
    if (request == null) {
      return null;
    }
    if (request.budgetName() != null) {
      budget.setBudgetName(request.budgetName());
    }
    if (request.description() != null) {
      budget.setDescription(request.description());
    }
    if (request.budgetLimit() != null) {
      budget.setBudgetLimit(request.budgetLimit());
    }
    if (request.amount() != null) {
      budget.setAmount(request.amount());
    }
    if (request.currency() != null) {
      budget.setCurrency(request.currency());
    }
    return budget;
  }

  private BudgetMapper() {}
}
