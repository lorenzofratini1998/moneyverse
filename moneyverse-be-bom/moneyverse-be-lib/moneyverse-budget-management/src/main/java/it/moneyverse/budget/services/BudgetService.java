package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import java.util.List;
import java.util.UUID;

public interface BudgetService {

  BudgetDto createBudget(BudgetRequestDto budgetDto);

  void createDefaultBudgets(UUID userId, String currency);

  List<BudgetDto> getBudgets(UUID userId, BudgetCriteria criteria);

  BudgetDto getBudget(UUID budgetId);
  BudgetDto updateBudget(UUID budgetId, BudgetUpdateRequestDto budgetDto);
  void deleteBudget(UUID budgetId);

  void deleteAllBudgets(UUID userId);
}
