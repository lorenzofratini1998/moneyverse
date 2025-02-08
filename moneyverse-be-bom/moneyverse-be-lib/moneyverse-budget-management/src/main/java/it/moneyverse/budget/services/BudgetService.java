package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BudgetService {
  BudgetDto createBudget(BudgetRequestDto request);

  List<BudgetDto> getBudgetsByUserId(UUID userId, BudgetCriteria criteria);

  BudgetDto getBudget(UUID budgetId);

  BudgetDto updateBudget(UUID budgetId, BudgetUpdateRequestDto request);

  void deleteBudget(UUID budgetId);

  void incrementBudgetAmount(UUID budgetId, BigDecimal amount);

  void decrementBudgetAmount(UUID budgetId, BigDecimal amount);
}
