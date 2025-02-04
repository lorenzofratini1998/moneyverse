package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID>, BudgetCustomRepository {
  Boolean existsByUserIdAndBudgetName(UUID userId, String budgetName);

  boolean existsByUserIdAndBudgetId(UUID userId, UUID budgetId);

  List<Budget> findBudgetByUserId(UUID userId);

  boolean existsByBudgetId(UUID budgetId);
}
