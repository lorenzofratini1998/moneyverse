package it.moneyverse.user.services;

import static it.moneyverse.user.utils.UserTestUtils.createPreferencesRequest;
import static it.moneyverse.user.utils.UserUtils.ONBOARD;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.repositories.PreferenceRepository;
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
  void givenUserIdAndPreferences_WhenCreatePreferences_ThenPreferencesCreated(
      @Mock List<Preference> preferences,
      @Mock PreferenceDto preferenceDto,
      @Mock UserDto userDto) {
    List<PreferenceRequest> request = createPreferencesRequest();
    UUID userId = RandomUtils.randomUUID();
    String attributeValue = RandomUtils.randomString(10);
    when(keycloakService.getUserById(userId)).thenReturn(Optional.of(userDto));
    preferenceMapper
        .when(() -> PreferenceMapper.toPreference(userId, request))
        .thenReturn(preferences);
    when(preferenceRepository.saveAll(preferences)).thenReturn(preferences);
    preferenceMapper
        .when(() -> PreferenceMapper.toPreferenceDto(userId, preferences))
        .thenReturn(preferenceDto);
    when(keycloakService.getUserAttributeValue(userId, ONBOARD))
        .thenReturn(Optional.of(attributeValue));

    PreferenceDto result = userManagementService.createPreferences(userId, request);

    assertNotNull(result);
    verify(keycloakService, times(1)).getUserById(userId);
    preferenceMapper.verify(() -> PreferenceMapper.toPreference(userId, request), times(1));
    verify(preferenceRepository, times(1)).saveAll(preferences);
    preferenceMapper.verify(() -> PreferenceMapper.toPreferenceDto(userId, preferences), times(1));
    verify(keycloakService, times(1)).getUserAttributeValue(userId, ONBOARD);
  }

  @Test
  void givenUserIdAndPreferences_WhenCreatePreferences_ThenUserNotFound() {
    List<PreferenceRequest> request = createPreferencesRequest();
    UUID userId = RandomUtils.randomUUID();
    when(keycloakService.getUserById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> userManagementService.createPreferences(userId, request));

    verify(keycloakService, times(1)).getUserById(userId);
    preferenceMapper.verify(() -> PreferenceMapper.toPreference(userId, request), never());
    verify(preferenceRepository, never()).saveAll(any());
    preferenceMapper.verify(() -> PreferenceMapper.toPreferenceDto(any(), any()), never());
  }
}
