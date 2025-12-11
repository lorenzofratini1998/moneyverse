package it.moneyverse.user.utils.mapper;

import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceItemDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PreferenceMapper {

  public static UserPreference toUserPreference(
      UUID userId, UserPreferenceRequest request, Preference preference) {
    UserPreference userPreference = new UserPreference();
    userPreference.setUserId(userId);
    userPreference.setValue(request.value());
    userPreference.setPreference(preference);
    return userPreference;
  }

  public static List<UserPreferenceDto> toUserPreferenceDto(List<UserPreference> userPreferences) {
    if (userPreferences.isEmpty()) {
      return Collections.emptyList();
    }
    return userPreferences.stream().map(PreferenceMapper::toUserPreferenceDto).toList();
  }

  public static UserPreferenceDto toUserPreferenceDto(UserPreference userPreference) {
    return UserPreferenceDto.builder()
        .withUserId(userPreference.getUserId())
        .withPreference(toPreferenceDto(userPreference.getPreference()))
        .withValue(userPreference.getValue())
        .build();
  }

  private static List<UserPreferenceItemDto> toUserPreferenceItemDto(
      List<UserPreference> preferences) {
    return preferences.stream()
        .map(PreferenceMapper::toUserPreferenceItemDto)
        .collect(Collectors.toList());
  }

  private static UserPreferenceItemDto toUserPreferenceItemDto(UserPreference userPreference) {
    if (userPreference == null) {
      return null;
    }
    return UserPreferenceItemDto.builder()
        .withUserPreferenceId(userPreference.getUserPreferenceId())
        .withValue(userPreference.getValue())
        .withPreference(toPreferenceDto(userPreference.getPreference()))
        .build();
  }

  public static List<PreferenceDto> toPreferenceDto(List<Preference> preferences) {
    if (preferences.isEmpty()) {
      return Collections.emptyList();
    }
    return preferences.stream().map(PreferenceMapper::toPreferenceDto).toList();
  }

  public static PreferenceDto toPreferenceDto(Preference preference) {
    if (preference == null) {
      return null;
    }
    return PreferenceDto.builder()
        .withPreferenceId(preference.getPreferenceId())
        .withName(preference.getName())
        .withMandatory(preference.getMandatory())
        .withUpdatable(preference.getUpdatable())
        .withDefaultValue(preference.getDefaultValue())
        .build();
  }

  private PreferenceMapper() {}
}
