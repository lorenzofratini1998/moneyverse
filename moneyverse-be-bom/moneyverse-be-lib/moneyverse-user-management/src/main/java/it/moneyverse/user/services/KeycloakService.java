package it.moneyverse.user.services;

import it.moneyverse.core.utils.properties.KeycloakProperties;
import it.moneyverse.user.exceptions.UserServiceException;
import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.utils.mapper.UserMapper;
import jakarta.ws.rs.NotFoundException;
import java.util.*;
import org.keycloak.admin.client.Keycloak;
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
    return Optional.ofNullable(UserMapper.toUserDto(getUserById(userId.toString())));
  }

  @Override
  public Optional<String> getUserAttributeValue(UUID userId, String key) {
    return Optional.ofNullable(getUserById(userId.toString()))
        .map(user -> user.getAttributes().getOrDefault(key, Collections.emptyList()))
        .filter(list -> !list.isEmpty())
        .map(List::getFirst)
        .filter(value -> !value.isEmpty());
  }

  @Override
  public void setUserAttribute(UUID userId, String key, String value) {
    Optional<UserRepresentation> userRepresentation =
        Optional.ofNullable(getUserById(userId.toString()));
    userRepresentation.ifPresent(
        user -> {
          LOGGER.info("Setting user attribute '{}' with value '{}'", key, value);
          Map<String, List<String>> attributes =
              Optional.ofNullable(user.getAttributes()).orElse(new HashMap<>());
          attributes.put(key, Collections.singletonList(value));
          user.setAttributes(attributes);
          keycloakClient
              .realm(properties.getRealmName())
              .users()
              .get(userId.toString())
              .update(user);
        });
  }

  private UserRepresentation getUserById(String userId) {
    try {
      return keycloakClient.realm(properties.getRealmName()).users().get(userId).toRepresentation();
    } catch (NotFoundException e) {
      LOGGER.warn("User with ID {} not found in realm '{}'.", userId, properties.getRealmName());
      return null;
    } catch (Exception e) {
      LOGGER.error(
          "An error occurred while checking if user exists with ID {}: {}", userId, e.getMessage());
      throw new UserServiceException("Failed to retrieve user with ID " + userId, e);
    }
  }
}
