package it.moneyverse.user.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.utils.properties.KeycloakProperties;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.exceptions.UserServiceException;
import it.moneyverse.user.model.UserTestFactory;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import it.moneyverse.user.utils.mapper.UserMapper;
import jakarta.ws.rs.NotFoundException;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

  private static final String REALM_NAME = RandomUtils.randomString(10);

  @InjectMocks KeycloakService keycloakService;

  @Mock Keycloak keycloakClient;
  @Mock KeycloakProperties keycloakProperties;
  @Mock RealmResource realm;
  @Mock UsersResource usersResource;
  @Mock UserResource userResource;
  MockedStatic<UserMapper> userMapper;

  @BeforeEach
  void setup() {
    userMapper = mockStatic(UserMapper.class);
    when(keycloakProperties.getRealmName()).thenReturn(REALM_NAME);
  }

  @AfterEach
  void tearDown() {
    userMapper.close();
  }

  @Test
  void givenUserId_WhenGetUserById_ThenReturnUser(
      @Mock UserRepresentation user, @Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenReturn(user);
    userMapper.when(() -> UserMapper.toUserDto(user)).thenReturn(userDto);

    Optional<UserDto> response = keycloakService.getUserById(userId);

    assertTrue(response.isPresent());
    verify(keycloakClient, times(1)).realm(REALM_NAME);
    verify(realm, times(1)).users();
    verify(usersResource, times(1)).get(userId.toString());
    verify(userResource, times(1)).toRepresentation();
    userMapper.verify(() -> UserMapper.toUserDto(user), times(1));
  }

  @Test
  void givenUserId_WhenGetUserById_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();
    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenThrow(NotFoundException.class);

    Optional<UserDto> response = keycloakService.getUserById(userId);

    assertFalse(response.isPresent());
    verify(keycloakClient, times(1)).realm(REALM_NAME);
    verify(realm, times(1)).users();
    verify(usersResource, times(1)).get(userId.toString());
    verify(userResource, times(1)).toRepresentation();
    userMapper.verify(() -> UserMapper.toUserDto(any(UserRepresentation.class)), never());
  }

  @Test
  void givenUserId_WhenGetUserByUsername_ThenReturnException() {
    when(keycloakClient.realm(REALM_NAME)).thenThrow(RuntimeException.class);

    assertThrows(
        UserServiceException.class, () -> keycloakService.getUserById(RandomUtils.randomUUID()));
  }

  @Test
  void givenUserId_WhenUpdateRequest_ThenReturnUpdateUser(
      @Mock UserRepresentation user, @Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    UserUpdateRequestDto request =
        UserTestFactory.UserUpdateRequestBuilder.builder().defaultInstance();

    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenReturn(user);
    userMapper.when(() -> UserMapper.toUserDto(user)).thenReturn(userDto);
    userMapper.when(() -> UserMapper.partialUpdate(user, request)).thenReturn(user);

    UserDto result = keycloakService.updateUser(userId, request);

    assertNotNull(result);
    verify(userResource, times(1)).update(user);
    verify(userResource, times(1)).sendVerifyEmail();
  }

  @Test
  void givenUserId_WhenDisableUser_ThenDisableUser(@Mock UserRepresentation user) {
    UUID userId = RandomUtils.randomUUID();

    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenReturn(user);

    keycloakService.disableUser(userId);

    verify(userResource, times(1)).update(user);
  }

  @Test
  void givenUserId_WhenDisableUser_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();

    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenThrow(NotFoundException.class);

    assertThrows(ResourceNotFoundException.class, () -> keycloakService.disableUser(userId));

    verify(userResource, never()).update(any(UserRepresentation.class));
  }

  @Test
  void givenUserId_WhenDeleteUser_ThenDeleteUser(@Mock UserRepresentation user) {
    UUID userId = RandomUtils.randomUUID();

    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenReturn(user);

    keycloakService.deleteUser(userId);

    verify(userResource, times(1)).remove();
  }

  @Test
  void givenUserId_WhenDeleteUser_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();

    when(keycloakClient.realm(REALM_NAME)).thenReturn(realm);
    when(realm.users()).thenReturn(usersResource);
    when(usersResource.get(userId.toString())).thenReturn(userResource);
    when(userResource.toRepresentation()).thenThrow(NotFoundException.class);

    assertThrows(ResourceNotFoundException.class, () -> keycloakService.deleteUser(userId));

    verify(userResource, never()).remove();
  }
}
