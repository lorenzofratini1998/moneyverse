package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, UUID>, BudgetCustomRepository {

  boolean existsByCategory_UserIdAndBudgetId(UUID categoryUserId, UUID budgetId);

  boolean existsByCategory_CategoryIdAndStartDateAndEndDate(
      UUID categoryCategoryId, LocalDate startDate, LocalDate endDate);
}
