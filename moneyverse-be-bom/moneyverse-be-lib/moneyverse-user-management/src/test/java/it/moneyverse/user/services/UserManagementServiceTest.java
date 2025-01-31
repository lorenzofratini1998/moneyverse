package it.moneyverse.user.services;

import static it.moneyverse.user.utils.UserTestUtils.createUserPreferenceRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserPreferenceDto;
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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

  @InjectMocks private UserManagementService userManagementService;

  @Mock KeycloakService keycloakService;
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
      @Mock UserPreferenceDto preferenceDto,
      @Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request = List.of(createUserPreferenceRequest(userId));
    when(keycloakService.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(preferenceRepository.findById(any(UUID.class))).thenReturn(Optional.of(preference));
    when(preference.getName()).thenReturn("STRING");
    preferenceMapper
        .when(() -> PreferenceMapper.toUserPreference(userId, request.getFirst(), preference))
        .thenReturn(userPreference);
    when(userPreferenceRepository.saveAll(List.of(userPreference)))
        .thenReturn(List.of(userPreference));
    preferenceMapper
        .when(() -> PreferenceMapper.toUserPreferenceDto(userId, List.of(userPreference)))
        .thenReturn(preferenceDto);

    UserPreferenceDto result = userManagementService.createUserPreferences(userId, request);

    assertNotNull(result);
    verify(keycloakService, times(1)).getUserById(userId);
    verify(preferenceRepository, times(1)).findById(any(UUID.class));
    preferenceMapper.verify(
        () -> PreferenceMapper.toUserPreference(userId, request.getFirst(), preference), times(1));
    verify(userPreferenceRepository, times(1)).saveAll(List.of(userPreference));
    preferenceMapper.verify(
        () -> PreferenceMapper.toUserPreferenceDto(userId, List.of(userPreference)), times(1));
  }

  @Test
  void givenUserIdAndPreferences_WhenCreateUserPreferences_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request = List.of(createUserPreferenceRequest(userId));
    when(keycloakService.getUserById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> userManagementService.createUserPreferences(userId, request));

    verify(keycloakService, times(1)).getUserById(userId);
    verify(preferenceRepository, never()).saveAll(any());
  }

  @Test
  void givenUserIdAndPreferences_WhenCreateUserPreferences_ThenPreferenceNotFound(
      @Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreferenceRequest> request = List.of(createUserPreferenceRequest(userId));
    when(keycloakService.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(preferenceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> userManagementService.createUserPreferences(userId, request));

    verify(keycloakService, times(1)).getUserById(userId);
    verify(preferenceRepository, times(1)).findById(any(UUID.class));
    verify(preferenceRepository, never()).saveAll(any());
  }

}
