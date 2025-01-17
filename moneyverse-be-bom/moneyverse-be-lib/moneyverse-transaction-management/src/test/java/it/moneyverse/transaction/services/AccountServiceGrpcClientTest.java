package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceGrpcClientTest {

  @Mock private AccountServiceGrpc.AccountServiceBlockingStub stub;
  @InjectMocks private AccountServiceGrpcClient accountServiceClient;

  @Test
  void givenAccountId_WhenCheckIfAccountExists_ThenReturnTrue() {
    UUID accountId = UUID.randomUUID();
    AccountResponse response = AccountResponse.newBuilder().setExists(true).build();
    when(stub.checkIfAccountExists(any(AccountRequest.class))).thenReturn(response);

    Boolean exists = accountServiceClient.checkIfAccountExists(accountId);

    assertTrue(exists);
    verify(stub, times(1)).checkIfAccountExists(any(AccountRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenCheckIfAccountExists_ThenFallbackMethodIsTriggered() {
    UUID accountId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Boolean exists = accountServiceClient.fallbackCheckIfAccountExists(accountId, throwable);

    assertFalse(exists);
  }
}
