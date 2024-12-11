package it.moneyverse.account.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc.UserServiceBlockingStub;
import it.moneyverse.test.utils.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserServiceGrpcClient}
 */
@ExtendWith(MockitoExtension.class)
class UserServiceGrpcClientTest {

  @InjectMocks private UserServiceGrpcClient userServiceClient;

  @Mock private UserServiceBlockingStub stub;

  @Test
  void givenUsername_WhenCheckIfUserExists_ThenReturnTrue(@Mock UserResponse response) {
    String username = RandomUtils.randomUUID().toString();
    when(stub.checkIfUserExists(any(UserRequest.class))).thenReturn(response);

    userServiceClient.checkIfUserExists(username);

    verify(response, times(1)).getExists();
  }

}
