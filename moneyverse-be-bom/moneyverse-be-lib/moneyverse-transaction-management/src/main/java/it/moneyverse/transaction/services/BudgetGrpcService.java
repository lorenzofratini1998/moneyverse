package it.moneyverse.transaction.services;

import static it.moneyverse.core.utils.constants.CacheConstants.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BudgetGrpcService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetGrpcService.class);
  private final BudgetServiceGrpc.BudgetServiceBlockingStub stub;

  public BudgetGrpcService(BudgetServiceGrpc.BudgetServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Cacheable(value = CATEGORIES_CACHE, key = "#categoryId", unless = "#result == null")
  @CircuitBreaker(
      name = BudgetServiceGrpcCircuitBreakerProperties.BUDGET_SERVICE_GRPC,
      fallbackMethod = "fallbackGetCategoryById")
  public Optional<CategoryDto> getCategoryById(UUID categoryId) {
    final CategoryResponse response =
        stub.getCategoryById(
            CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build());
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        CategoryDto.builder()
            .withCategoryId(UUID.fromString(response.getCategoryId()))
            .withUserId(UUID.fromString(response.getUserId()))
            .withDescription(response.getDescription())
            .withCategoryName(response.getCategoryName())
            .build());
  }

  private boolean isEmptyResponse(CategoryResponse response) {
    return response.getCategoryId().isEmpty()
        && response.getUserId().isEmpty()
        && response.getCategoryName().isEmpty()
        && response.getDescription().isEmpty();
  }

  Optional<CategoryDto> fallbackGetCategoryById(UUID categoryId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the BudgetService to check whether the category {} exists. Returning FALSE as fallback: {}",
        categoryId,
        throwable.getMessage());
    return Optional.empty();
  }

  @Cacheable(
      value = BUDGETS_CACHE,
      key = "#categoryId + '_' + #date.toString()",
      unless = "#result == null")
  @CircuitBreaker(
      name = BudgetServiceGrpcCircuitBreakerProperties.BUDGET_SERVICE_GRPC,
      fallbackMethod = "fallbackGetBudgetByCategoryIdAndDate")
  public Optional<BudgetDto> getBudgetByCategoryIdAndDate(UUID categoryId, LocalDate date) {
    BudgetRequest request =
        BudgetRequest.newBuilder()
            .setCategoryId(categoryId.toString())
            .setDate(date.toString())
            .build();
    return getBudget(request);
  }

  @Cacheable(value = BUDGETS_CACHE, key = "#budgetId", unless = "#result == null")
  @CircuitBreaker(
      name = BudgetServiceGrpcCircuitBreakerProperties.BUDGET_SERVICE_GRPC,
      fallbackMethod = "fallbackGetBudgetByBudgetId")
  public Optional<BudgetDto> getBudgetByBudgetId(UUID budgetId) {
    BudgetRequest request = BudgetRequest.newBuilder().setBudgetId(budgetId.toString()).build();
    return getBudget(request);
  }

  private Optional<BudgetDto> getBudget(BudgetRequest request) {
    final BudgetResponse response = stub.getBudget(request);
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        BudgetDto.builder()
            .withBudgetId(UUID.fromString(response.getBudgetId()))
            .withCategory(
                CategoryDto.builder()
                    .withCategoryId(UUID.fromString(response.getCategoryId()))
                    .build())
            .withStartDate(LocalDate.parse(response.getStartDate()))
            .withEndDate(LocalDate.parse(response.getEndDate()))
            .build());
  }

  private boolean isEmptyResponse(BudgetResponse response) {
    return response.getBudgetId().isEmpty()
        && response.getCategoryId().isEmpty()
        && response.getStartDate().isEmpty()
        && response.getEndDate().isEmpty();
  }

  Optional<BudgetDto> fallbackGetBudgetByCategoryIdAndDate(
      UUID categoryId, LocalDate date, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the BudgetService to check if a budget for the category {} and date {} exists. Returning EMPTY as fallback: {}",
        categoryId,
        date,
        throwable.getMessage());
    return Optional.empty();
  }

  Optional<BudgetDto> fallbackGetBudgetByBudgetId(UUID budgetId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the BudgetService to check whether the budget {} exists. Returning EMPTY as fallback: {}",
        budgetId,
        throwable.getMessage());
    return Optional.empty();
  }
}
