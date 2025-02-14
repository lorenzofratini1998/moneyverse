package it.moneyverse.user.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.test.utils.RandomUtils;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

  @InjectMocks private UserManagementService userManagementService;

  @Mock private KeycloakService keycloakService;
  @Mock private MessageProducer<UUID, String> messageProducer;

  @Test
  void givenUserId_WhenGetUser_ThenReturnUser(@Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    when(keycloakService.getUserById(userId)).thenReturn(Optional.of(userDto));

    UserDto result = userManagementService.getUser(userId);

    assertNotNull(result);
  }

  @Test
  void givenUserId_WhenGetUser_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();
    when(keycloakService.getUserById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userManagementService.getUser(userId));
  }

  @Test
  void givenUserId_WhenDeleteUser_ThenDeleteUser() {
    UUID userId = RandomUtils.randomUUID();
    Mockito.doNothing().when(keycloakService).deleteUser(userId);

    userManagementService.deleteUser(userId);

    verify(keycloakService, times(1)).deleteUser(userId);
    verify(messageProducer, times(1)).send(any(UserDeletionEvent.class), any(String.class));
  }

  @Test
  void givenUserId_WhenDeleteUser_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();
    doThrow(ResourceNotFoundException.class).when(keycloakService).deleteUser(userId);

    assertThrows(ResourceNotFoundException.class, () -> userManagementService.deleteUser(userId));

    verify(keycloakService, times(1)).deleteUser(userId);
    verify(messageProducer, never()).send(any(UserDeletionEvent.class), any(String.class));
  }
}
