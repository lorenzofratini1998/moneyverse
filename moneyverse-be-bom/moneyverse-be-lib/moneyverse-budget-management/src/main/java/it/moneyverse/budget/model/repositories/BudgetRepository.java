package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BudgetRepository extends JpaRepository<Budget, UUID>, BudgetCustomRepository {

  boolean existsByCategory_UserIdAndBudgetId(UUID categoryUserId, UUID budgetId);

  boolean existsByCategory_CategoryIdAndStartDateAndEndDate(
      UUID categoryCategoryId, LocalDate startDate, LocalDate endDate);

  @Query(
      "SELECT b FROM Budget b WHERE b.category.categoryId = :categoryId AND :date BETWEEN b.startDate AND b.endDate")
  Optional<Budget> findBudgetByCategoryAndDate(UUID categoryId, LocalDate date);
}
