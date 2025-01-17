package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.grpc.lib.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceGrpcClientTest {

  @Mock private BudgetServiceGrpc.BudgetServiceBlockingStub stub;
  @InjectMocks private BudgetServiceGrpcClient budgetServiceClient;

  @Test
  void givenBudgetId_WhenCheckIfBudgetExists_ThenReturnTrue() {
    UUID budgetId = UUID.randomUUID();
    BudgetResponse response = BudgetResponse.newBuilder().setExists(true).build();
    when(stub.checkIfBudgetExists(any(BudgetRequest.class))).thenReturn(response);

    Boolean exists = budgetServiceClient.checkIfBudgetExists(budgetId);

    assertTrue(exists);
    verify(stub, times(1)).checkIfBudgetExists(any(BudgetRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenCheckIfBudgetExists_ThenFallbackMethodIsTriggered() {
    UUID budgetId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Boolean exists = budgetServiceClient.fallbackCheckIfBudgetExists(budgetId, throwable);

    assertFalse(exists);
  }
}
