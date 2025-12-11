package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import java.util.List;
import java.util.UUID;

public interface BudgetCustomRepository {
  List<Budget> filterBudgets(UUID userId, BudgetCriteria param);
}
