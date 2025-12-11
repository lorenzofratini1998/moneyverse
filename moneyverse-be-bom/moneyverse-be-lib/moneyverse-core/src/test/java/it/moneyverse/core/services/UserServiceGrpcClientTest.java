package it.moneyverse.core.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.UserDto;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link UserServiceGrpcClient} */
@ExtendWith(MockitoExtension.class)
class UserServiceGrpcClientTest {

  @Mock private UserGrpcService userGrpcService;

  @InjectMocks private UserServiceGrpcClient userServiceClient;

  @Test
  void testGetUserById(@Mock UserDto userDto) {
    UUID userId = UUID.randomUUID();
    when(userGrpcService.getUserById(userId)).thenReturn(Optional.of(userDto));

    Optional<UserDto> response = userServiceClient.getUserById(userId);

    assertTrue(response.isPresent());
    verify(userGrpcService, times(1)).getUserById(userId);
  }

  @Test
  void testCheckIfUserStillExists() {
    UUID userId = UUID.randomUUID();
    when(userGrpcService.getUserById(userId)).thenReturn(Optional.empty());
    assertDoesNotThrow(() -> userServiceClient.checkIfUserStillExist(userId));
    verify(userGrpcService, times(1)).getUserById(userId);
  }

  @Test
  void testCheckIfUserStillExists_Exception(@Mock UserDto userDto) {
    UUID userId = UUID.randomUUID();
    when(userGrpcService.getUserById(userId)).thenReturn(Optional.of(userDto));
    assertThrows(
        ResourceStillExistsException.class, () -> userServiceClient.checkIfUserStillExist(userId));
    verify(userGrpcService, times(1)).getUserById(userId);
  }
}
