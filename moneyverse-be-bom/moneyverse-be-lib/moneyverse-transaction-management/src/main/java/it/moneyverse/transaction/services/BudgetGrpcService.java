package it.moneyverse.transaction.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
import it.moneyverse.grpc.lib.CategoryRequest;
import it.moneyverse.grpc.lib.CategoryResponse;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BudgetGrpcService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetGrpcService.class);
  private final BudgetServiceGrpc.BudgetServiceBlockingStub stub;

  public BudgetGrpcService(BudgetServiceGrpc.BudgetServiceBlockingStub stub) {
    this.stub = stub;
  }

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
}
