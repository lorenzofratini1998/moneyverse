package it.moneyverse.transaction.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
import it.moneyverse.grpc.lib.CategoryRequest;
import it.moneyverse.grpc.lib.CategoryResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceGrpcClient implements BudgetServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetServiceGrpcClient.class);

  private final BudgetServiceGrpc.BudgetServiceBlockingStub stub;

  public BudgetServiceGrpcClient(BudgetServiceGrpc.BudgetServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  @CircuitBreaker(
      name = BudgetServiceGrpcCircuitBreakerProperties.BUDGET_SERVICE_GRPC,
      fallbackMethod = "fallbackCheckIfCategoryExists")
  public Boolean checkIfCategoryExists(UUID categoryId) {
    final CategoryResponse response =
        stub.checkIfCategoryExists(
            CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build());
    return response.getExists();
  }

  Boolean fallbackCheckIfCategoryExists(UUID categoryId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the BudgetService to check whether the category {} exists. Returning FALSE as fallback: {}",
        categoryId,
        throwable.getMessage());
    return false;
  }
}
