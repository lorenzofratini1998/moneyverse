package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;

public interface BudgetService {

  BudgetDto createBudget(BudgetRequestDto budgetDto);
}
