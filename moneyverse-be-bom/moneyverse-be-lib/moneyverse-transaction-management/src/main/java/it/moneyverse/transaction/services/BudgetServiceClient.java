package it.moneyverse.transaction.services;

import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BudgetServiceClient {
  Optional<CategoryDto> getCategoryById(UUID categoryId);

  Optional<BudgetDto> getBudgetByCategoryIdAndDate(UUID categoryId, LocalDate date);

  void checkIfCategoryExists(UUID categoryId);

  void checkIfCategoryStillExists(UUID categoryId);
}
