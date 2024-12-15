package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import jakarta.validation.Valid;

public interface BudgetOperations {
  BudgetDto createBudget(@Valid BudgetRequestDto request);
}
