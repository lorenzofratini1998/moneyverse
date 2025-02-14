package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.CategoryDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceGrpcClient implements BudgetServiceClient {

  private final BudgetGrpcService budgetGrpcService;

  public BudgetServiceGrpcClient(BudgetGrpcService budgetGrpcService) {
    this.budgetGrpcService = budgetGrpcService;
  }

  @Override
  public Optional<CategoryDto> getCategoryById(UUID categoryId) {
    return budgetGrpcService.getCategoryById(categoryId);
  }

  @Override
  public void checkIfCategoryExists(UUID categoryId) {
    if (budgetGrpcService.getCategoryById(categoryId).isEmpty()) {
      throw new ResourceNotFoundException("Category %s does not exists".formatted(categoryId));
    }
  }

  @Override
  public void checkIfCategoryStillExists(UUID categoryId) {
    if (budgetGrpcService.getCategoryById(categoryId).isPresent()) {
      throw new ResourceStillExistsException(
          "Category %s still exists in the system".formatted(categoryId));
    }
  }
}
