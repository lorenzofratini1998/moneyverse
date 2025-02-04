package it.moneyverse.user.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService implements UserService {

  private final KeycloakService keycloakService;
  private final MessageProducer<UUID, String> messageProducer;

  public UserManagementService(
      KeycloakService keycloakService, MessageProducer<UUID, String> messageProducer) {
    this.keycloakService = keycloakService;
    this.messageProducer = messageProducer;
  }

  @Override
  public UserDto getUser(UUID userId) {
    return keycloakService
        .getUserById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with id %s not found.".formatted(userId)));
  }

  @Override
  public UserDto updateUser(UUID userId, UserUpdateRequestDto request) {
    return keycloakService.updateUser(userId, request);
  }

  @Override
  public void disableUser(UUID userId) {
    keycloakService.disableUser(userId);
  }

  @Override
  public void deleteUser(UUID userId) {
    keycloakService.deleteUser(userId);
    messageProducer.send(new UserDeletionEvent(userId), UserDeletionTopic.TOPIC);
  }
}
