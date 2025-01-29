package it.moneyverse.user.utils.mapper;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceItemDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PreferenceMapper {

  public static List<Preference> toPreference(UUID userId, List<PreferenceRequest> request) {
    return request.stream()
        .map(
            preferenceItem -> {
              Preference preference = new Preference();
              preference.setUserId(userId);
              preference.setKey(preferenceItem.key());
              preference.setValue(preferenceItem.value());
              return preference;
            })
        .toList();
  }

  public static PreferenceDto toPreferenceDto(UUID userId, List<Preference> preferences) {
    if (preferences.isEmpty()) {
      return null;
    }
    return PreferenceDto.builder()
        .withUserId(userId)
        .withPreferences(toPreferenceItemDto(preferences))
        .build();
  }

  private static List<PreferenceItemDto> toPreferenceItemDto(List<Preference> preferences) {
    if (preferences == null) {
      return null;
    }
    return preferences.stream()
        .map(PreferenceMapper::toPreferenceItemDto)
        .collect(Collectors.toList());
  }

  private static PreferenceItemDto toPreferenceItemDto(Preference preference) {
    if (preference == null) {
      return null;
    }
    return PreferenceItemDto.builder()
        .withKey(preference.getKey())
        .withValue(preference.getValue())
        .withValue(preference.getValue())
        .build();
  }

  private PreferenceMapper() {}
}
