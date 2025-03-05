package it.moneyverse.user.services;

import static it.moneyverse.core.utils.constants.CacheConstants.USERS_CACHE;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
  @CachePut(value = USERS_CACHE, key = "#userId", unless = "#result == null")
  public UserDto updateUser(UUID userId, UserUpdateRequestDto request) {
    return keycloakService.updateUser(userId, request);
  }

  @Override
  @CacheEvict(value = USERS_CACHE, key = "#userId")
  public void disableUser(UUID userId) {
    keycloakService.disableUser(userId);
  }

  @Override
  @CacheEvict(value = USERS_CACHE, key = "#userId")
  public void deleteUser(UUID userId) {
    keycloakService.deleteUser(userId);
    messageProducer.send(UserEvent.builder().withUserId(userId).build(), UserDeletionTopic.TOPIC);
  }

  @Override
  public void checkIfUserExists(UUID userId) {
    getUser(userId);
  }
}
