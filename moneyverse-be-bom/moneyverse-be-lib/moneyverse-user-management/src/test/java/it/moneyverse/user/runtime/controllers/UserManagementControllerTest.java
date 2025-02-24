package it.moneyverse.user.runtime.controllers;

import static it.moneyverse.user.utils.UserTestUtils.createUserPreferenceRequest;
import static it.moneyverse.user.utils.UserTestUtils.createUserUpdateRequest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.core.boot.CurrencyServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.test.runtime.processor.MockUserRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import it.moneyverse.user.services.PreferenceManagementService;
import it.moneyverse.user.services.UserManagementService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    controllers = UserManagementController.class,
    excludeAutoConfiguration = {
      DataSourceAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
@ExtendWith(MockitoExtension.class)
class UserManagementControllerTest {

  @Value("${spring.security.base-path}")
  String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private PreferenceManagementService preferenceManagementService;
  @MockitoBean private UserManagementService userManagementService;

  @Test
  void testCreateUserPreferences_Success(@Mock UserPreferenceDto preferenceDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request =
        Collections.singletonList(createUserPreferenceRequest(userId));
    when(preferenceManagementService.createUserPreferences(userId, request))
        .thenReturn(preferenceDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/users/{userId}/preferences", userId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isCreated());
  }

  @ParameterizedTest
  @MethodSource("it.moneyverse.user.utils.UserTestUtils#invalidPreferencesRequestProvider")
  void testCreateUserPreferences_BadRequest(Supplier<List<UserPreferenceRequest>> requestSupplier)
      throws Exception {
    UUID userId = RandomUtils.randomUUID();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/users/{userId}/preferences", userId)
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isBadRequest());
    verify(preferenceManagementService, never())
        .createUserPreferences(userId, requestSupplier.get());
  }

  @Test
  void testGetUser_Success(@Mock UserDto userDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();

    when(userManagementService.getUser(userId)).thenReturn(userDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isOk());
  }

  @Test
  void testGetUser_UserNotFound() throws Exception {
    UUID userId = RandomUtils.randomUUID();

    when(userManagementService.getUser(userId)).thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateUser_Success(@Mock UserDto userDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    UserUpdateRequestDto request = createUserUpdateRequest();
    when(userManagementService.updateUser(userId, request)).thenReturn(userDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/users/{userId}", userId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateUser_UserNotFound() throws Exception {
    UUID userId = RandomUtils.randomUUID();
    UserUpdateRequestDto request = createUserUpdateRequest();
    when(userManagementService.updateUser(userId, request))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/users/{userId}", userId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDisableUser_Success() throws Exception {
    UUID userId = RandomUtils.randomUUID();
    Mockito.doNothing().when(userManagementService).disableUser(userId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(basePath + "/users/{userId}/disable", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDisableUser_UserNotFound() throws Exception {
    UUID userId = RandomUtils.randomUUID();
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(userManagementService)
        .disableUser(userId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(basePath + "/users/{userId}/disable", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteUser_Success() throws Exception {
    UUID userId = RandomUtils.randomUUID();
    Mockito.doNothing().when(userManagementService).deleteUser(userId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteUser_UserNotFound() throws Exception {
    UUID userId = RandomUtils.randomUUID();
    Mockito.doThrow(ResourceNotFoundException.class).when(userManagementService).deleteUser(userId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isNotFound());
  }
}
