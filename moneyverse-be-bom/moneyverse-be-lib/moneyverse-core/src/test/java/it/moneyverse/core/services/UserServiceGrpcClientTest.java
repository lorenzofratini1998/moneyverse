package it.moneyverse.core.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for {@link UserServiceGrpcClient} */
@ExtendWith(MockitoExtension.class)
class UserServiceGrpcClientTest {

  @Mock private UserServiceGrpc.UserServiceBlockingStub stub;

  @InjectMocks private UserServiceGrpcClient userServiceClient;

  @Test
  void givenUsername_WhenCheckIfUserExists_ThenReturnTrue() {
    String username = UUID.randomUUID().toString();
    UserResponse mockResponse = UserResponse.newBuilder().setExists(true).build();
    when(stub.checkIfUserExists(any(UserRequest.class))).thenReturn(mockResponse);

    Boolean exists = userServiceClient.checkIfUserExists(username);

    assertTrue(exists);
    verify(stub, times(1)).checkIfUserExists(any(UserRequest.class));
  }

  @Test
  void givenCircuitBreakerOpen_WhenCheckIfUserExists_ThenFallbackMethodIsTriggered() {
    String username = UUID.randomUUID().toString();
    Throwable throwable = mock(CallNotPermittedException.class);

    Boolean exists = userServiceClient.fallbackCheckIfUserExists(username, throwable);

    assertFalse(exists);
  }
}
