package it.moneyverse.user.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.PreferenceTestFactory;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PreferenceMapperTest {

  @Test
  void testToUserPreference() {
    UUID userId = RandomUtils.randomUUID();
    Preference preference = PreferenceTestFactory.fakePreference();
    UserPreferenceRequest userPreferenceRequest =
        PreferenceTestFactory.UserPreferenceRequestBuilder.builder()
            .withPreferenceId(preference.getPreferenceId())
            .build();

    UserPreference userPreference =
        PreferenceMapper.toUserPreference(userId, userPreferenceRequest, preference);

    assertEquals(userId, userPreference.getUserId());
    assertEquals(userPreferenceRequest.value(), userPreference.getValue());
    assertEquals(preference, userPreference.getPreference());
  }

  @Test
  void testToUserPreferenceDto_NonEmptyList() {
    UUID userId = RandomUtils.randomUUID();
    List<UserPreference> userPreferences =
        List.of(
            PreferenceTestFactory.fakeUserPreference(
                userId, PreferenceTestFactory.fakePreference()));

    List<UserPreferenceDto> userPreferenceDto =
        PreferenceMapper.toUserPreferenceDto(userPreferences);

    assertEquals(1, userPreferenceDto.size());
  }

  @Test
  void testToUserPreferenceDto_EmptyList() {
    List<UserPreference> userPreferences = Collections.emptyList();

    List<UserPreferenceDto> userPreferenceDto =
        PreferenceMapper.toUserPreferenceDto(userPreferences);

    assertEquals(0, userPreferenceDto.size());
  }

  @Test
  void testToPreferenceDto_NullPreference() {
    assertNull(PreferenceMapper.toPreferenceDto((Preference) null));
  }

  @Test
  void testToPreferenceDto_Preference() {
    Preference preference = PreferenceTestFactory.fakePreference();

    PreferenceDto preferenceDto = PreferenceMapper.toPreferenceDto(preference);

    assertEquals(preference.getPreferenceId(), preferenceDto.getPreferenceId());
    assertEquals(preference.getName(), preferenceDto.getName());
    assertEquals(preference.getDefaultValue(), preferenceDto.getDefaultValue());
    assertEquals(preference.getMandatory(), preferenceDto.getMandatory());
    assertEquals(preference.getUpdatable(), preferenceDto.getUpdatable());
  }

  @Test
  void testToPreferenceDto_EmptyList() {
    assertEquals(Collections.emptyList(), PreferenceMapper.toPreferenceDto(List.of()));
  }

  @Test
  void testToPreferenceDto_NonEmptyList() {
    List<Preference> preferences = List.of(PreferenceTestFactory.fakePreference());

    List<PreferenceDto> preferenceDtos = PreferenceMapper.toPreferenceDto(preferences);

    assertEquals(preferences.size(), preferenceDtos.size());
  }
}
