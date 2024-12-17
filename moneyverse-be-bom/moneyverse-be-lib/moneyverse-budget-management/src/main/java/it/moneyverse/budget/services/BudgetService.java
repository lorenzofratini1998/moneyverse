package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;

import java.util.List;
import java.util.UUID;

public interface BudgetService {

  BudgetDto createBudget(BudgetRequestDto budgetDto);
  List<BudgetDto> getBudgets(BudgetCriteria criteria);
  BudgetDto getBudget(UUID budgetId);
}
