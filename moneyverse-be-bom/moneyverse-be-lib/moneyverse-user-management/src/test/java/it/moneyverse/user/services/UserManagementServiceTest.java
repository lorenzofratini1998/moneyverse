package it.moneyverse.user.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserDto;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

  @InjectMocks private UserManagementService userManagementService;

  @Mock private KeycloakService keycloakService;

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
}
