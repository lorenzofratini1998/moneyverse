package it.moneyverse.core.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link UserServiceGrpcClient} */
@ExtendWith(MockitoExtension.class)
class UserServiceGrpcClientTest {

  @Mock private UserServiceGrpc.UserServiceBlockingStub stub;

  @InjectMocks private UserServiceGrpcClient userServiceClient;

  @Test
  void givenUsername_WhenCheckIfUserExists_ThenReturnTrue() {
    UUID userId = UUID.randomUUID();
    UserResponse mockResponse = UserResponse.newBuilder().setExists(true).build();
    when(stub.checkIfUserExists(any(UserRequest.class))).thenReturn(mockResponse);

    Boolean exists = userServiceClient.checkIfUserExists(userId);

    assertTrue(exists);
    verify(stub, times(1)).checkIfUserExists(any(UserRequest.class));
  }

  @Test
  void givenCircuitBreakerOpen_WhenCheckIfUserExists_ThenFallbackMethodIsTriggered() {
    UUID userId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Boolean exists = userServiceClient.fallbackCheckIfUserExists(userId, throwable);

    assertFalse(exists);
  }
}
