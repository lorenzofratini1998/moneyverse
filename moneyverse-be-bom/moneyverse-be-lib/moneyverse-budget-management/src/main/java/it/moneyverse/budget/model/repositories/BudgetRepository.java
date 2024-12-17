package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID>, BudgetCustomRepository {
  Object existsByUsernameAndBudgetName(String username, String budgetName);
}
