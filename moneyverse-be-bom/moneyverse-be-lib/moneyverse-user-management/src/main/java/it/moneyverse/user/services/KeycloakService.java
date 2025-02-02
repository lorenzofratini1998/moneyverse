package it.moneyverse.user.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.utils.properties.KeycloakProperties;
import it.moneyverse.user.exceptions.UserServiceException;
import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import it.moneyverse.user.utils.mapper.UserMapper;
import jakarta.ws.rs.NotFoundException;
import java.util.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakService implements AuthenticationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);

  private final Keycloak keycloakClient;
  private final KeycloakProperties properties;

  public KeycloakService(Keycloak keycloakClient, KeycloakProperties properties) {
    this.keycloakClient = keycloakClient;
    this.properties = properties;
  }

  @Override
  public Optional<UserDto> getUserById(UUID userId) {
    return getUserRepresentation(userId).map(UserMapper::toUserDto);
  }

  @Override
  public UserDto updateUser(UUID userId, UserUpdateRequestDto request) {
    UserRepresentation userRepresentation = getUser(userId);

    userRepresentation = UserMapper.partialUpdate(userRepresentation, request);
    getUsersResource().get(userId.toString()).update(userRepresentation);
    if (request.email() != null) {
      sendVerificationEmail(userId.toString());
    }
    return UserMapper.toUserDto(userRepresentation);
  }

  @Override
  public void disableUser(UUID userId) {
    UserRepresentation userRepresentation = getUser(userId);

    userRepresentation.setEnabled(false);
    getUsersResource().get(userId.toString()).update(userRepresentation);
  }

  @Override
  public void deleteUser(UUID userId) {
    getUser(userId);
    getUsersResource().get(userId.toString()).remove();
  }

  private UserRepresentation getUser(UUID userId) {
    return getUserRepresentation(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with ID %s not found.".formatted(userId)));
  }

  private Optional<UserRepresentation> getUserRepresentation(UUID userId) {
    try {
      UserRepresentation representation =
          getUsersResource().get(userId.toString()).toRepresentation();
      return Optional.ofNullable(representation);
    } catch (NotFoundException e) {
      LOGGER.warn("User with ID {} not found in realm '{}'.", userId, properties.getRealmName());
      return Optional.empty();
    } catch (Exception e) {
      LOGGER.error(
          "An error occurred while retrieving user with ID {}: {}", userId, e.getMessage());
      throw new UserServiceException("Failed to retrieve user with ID " + userId, e);
    }
  }

  private UsersResource getUsersResource() {
    return keycloakClient.realm(properties.getRealmName()).users();
  }

  private void sendVerificationEmail(String userId) {
    try {
      getUsersResource().get(userId).sendVerifyEmail();
    } catch (Exception e) {
      throw new UserServiceException(
          "Failed to send verification email for user with ID " + userId, e);
    }
  }
}
