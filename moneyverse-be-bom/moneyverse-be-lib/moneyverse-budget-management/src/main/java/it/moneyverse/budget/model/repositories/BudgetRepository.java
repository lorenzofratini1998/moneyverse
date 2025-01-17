package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID>, BudgetCustomRepository {
  Boolean existsByUsernameAndBudgetName(String username, String budgetName);

  boolean existsByUsernameAndBudgetId(String username, UUID budgetId);

  List<Budget> findBudgetByUsername(String username);

  boolean existsByBudgetId(UUID budgetId);
}
