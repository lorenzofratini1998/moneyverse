package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.core.model.dto.BudgetDto;
import java.util.List;
import java.util.UUID;

public interface BudgetOperations {
  BudgetDto createBudget(BudgetRequestDto request);

  List<BudgetDto> getBudgetsByUserId(UUID userId, BudgetCriteria criteria);

  BudgetDto getBudget(UUID budgetId);

  BudgetDto updateBudget(UUID budgetId, BudgetUpdateRequestDto request);

  void deleteBudget(UUID budgetId);
}
