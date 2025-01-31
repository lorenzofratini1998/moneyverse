package it.moneyverse.user.runtime.controllers;

import static it.moneyverse.user.utils.UserTestUtils.createUserPreferenceRequest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.core.boot.CurrencyServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.test.runtime.processor.MockUserRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
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
  @MockitoBean private UserManagementService userManagementService;

  @Test
  void testCreatePreferences_Success(@Mock UserPreferenceDto preferenceDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request =
        Collections.singletonList(createUserPreferenceRequest(userId));
    when(userManagementService.createUserPreferences(userId, request)).thenReturn(preferenceDto);

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
  void testCreatePreferences_BadRequest(Supplier<List<UserPreferenceRequest>> requestSupplier)
      throws Exception {
    UUID userId = RandomUtils.randomUUID();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/users/{userId}/preferences", userId)
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isBadRequest());
    verify(userManagementService, never()).createUserPreferences(userId, requestSupplier.get());
  }
}
