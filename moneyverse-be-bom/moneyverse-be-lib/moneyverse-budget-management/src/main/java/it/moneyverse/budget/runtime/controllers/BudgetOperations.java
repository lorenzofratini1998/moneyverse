package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface BudgetOperations {
  BudgetDto createBudget(@Valid BudgetRequestDto request);
  List<BudgetDto> getBudgets(BudgetCriteria criteria);
  BudgetDto getBudget(UUID budgetId);
  BudgetDto updateBudget(UUID budgetId, @Valid BudgetUpdateRequestDto request);
  void deleteBudget(UUID budgetId);
}
