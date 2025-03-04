package it.moneyverse.user.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.PreferenceTestFactory;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import it.moneyverse.user.model.repositories.PreferenceRepository;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import it.moneyverse.user.utils.mapper.PreferenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreferenceManagementServiceTest {

  @InjectMocks private PreferenceManagementService preferenceManagementService;

  @Mock UserService userService;
  @Mock PreferenceRepository preferenceRepository;
  @Mock UserPreferenceRepository userPreferenceRepository;
  private MockedStatic<PreferenceMapper> preferenceMapper;

  @BeforeEach
  void setUp() {
    preferenceMapper = mockStatic(PreferenceMapper.class);
  }

  @AfterEach
  void tearDown() {
    preferenceMapper.close();
  }

  @Test
  void givenUserIdAndUserPreferences_WhenCreatePreferences_ThenUserPreferencesCreated(
      @Mock Preference preference,
      @Mock UserPreference userPreference,
      @Mock UserPreferenceDto preferenceDto) {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request =
        List.of(PreferenceTestFactory.UserPreferenceRequestBuilder.defaultInstance());
    Mockito.doNothing().when(userService).checkIfUserExists(userId);
    when(preferenceRepository.findById(any(UUID.class))).thenReturn(Optional.of(preference));
    when(preference.getName()).thenReturn("STRING");
    preferenceMapper
        .when(() -> PreferenceMapper.toUserPreference(userId, request.getFirst(), preference))
        .thenReturn(userPreference);
    when(userPreferenceRepository.saveAll(List.of(userPreference)))
        .thenReturn(List.of(userPreference));
    preferenceMapper
        .when(() -> PreferenceMapper.toUserPreferenceDto(List.of(userPreference)))
        .thenReturn(List.of(preferenceDto));

    List<UserPreferenceDto> result =
        preferenceManagementService.createUserPreferences(userId, request);

    assertNotNull(result);
    verify(userService, times(1)).checkIfUserExists(userId);
    verify(preferenceRepository, times(1)).findById(any(UUID.class));
    verify(userPreferenceRepository, times(1)).saveAll(List.of(userPreference));
  }

  @Test
  void givenUserIdAndPreferences_WhenCreateUserPreferences_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request =
        List.of(PreferenceTestFactory.UserPreferenceRequestBuilder.defaultInstance());
    Mockito.doThrow(ResourceNotFoundException.class).when(userService).checkIfUserExists(userId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> preferenceManagementService.createUserPreferences(userId, request));

    verify(userService, times(1)).checkIfUserExists(userId);
    verify(preferenceRepository, never()).saveAll(any());
  }

  @Test
  void givenUserIdAndPreferences_WhenCreateUserPreferences_ThenPreferenceNotFound() {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request =
        List.of(PreferenceTestFactory.UserPreferenceRequestBuilder.defaultInstance());
    Mockito.doNothing().when(userService).checkIfUserExists(userId);
    when(preferenceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> preferenceManagementService.createUserPreferences(userId, request));

    verify(userService, times(1)).checkIfUserExists(userId);
    verify(preferenceRepository, times(1)).findById(any(UUID.class));
    verify(preferenceRepository, never()).saveAll(any());
  }
}
