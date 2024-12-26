package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;

import java.util.List;

public interface BudgetCustomRepository {
    List<Budget> findBudgets(BudgetCriteria param);
}
