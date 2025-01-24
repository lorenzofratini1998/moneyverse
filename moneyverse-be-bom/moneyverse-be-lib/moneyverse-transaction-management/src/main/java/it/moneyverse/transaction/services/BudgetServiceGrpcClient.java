package it.moneyverse.transaction.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.BudgetRequest;
import it.moneyverse.grpc.lib.BudgetResponse;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
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
      fallbackMethod = "fallbackCheckIfBudgetExists")
  public Boolean checkIfBudgetExists(UUID budgetId) {
    final BudgetResponse response =
        stub.checkIfBudgetExists(
            BudgetRequest.newBuilder().setBudgetId(budgetId.toString()).build());
    return response.getExists();
  }

  Boolean fallbackCheckIfBudgetExists(UUID budgetId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the BudgetService to check whether the budget {} exists. Returning FALSE as fallback: {}",
        budgetId,
        throwable.getMessage());
    return false;
  }
}
