package it.moneyverse.transaction.services;

import it.moneyverse.core.model.dto.CategoryDto;
import java.util.Optional;
import java.util.UUID;

public interface BudgetServiceClient {
  Optional<CategoryDto> getCategoryById(UUID categoryId);

  void checkIfCategoryExists(UUID categoryId);

  void checkIfCategoryStillExists(UUID categoryId);
}
