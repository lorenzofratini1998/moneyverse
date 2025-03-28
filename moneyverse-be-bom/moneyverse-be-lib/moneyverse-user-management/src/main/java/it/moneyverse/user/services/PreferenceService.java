package it.moneyverse.user.services;

import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import java.util.List;
import java.util.UUID;

public interface PreferenceService {
  List<UserPreferenceDto> createUserPreferences(UUID userId, List<UserPreferenceRequest> request);

  List<UserPreferenceDto> getUserPreferences(UUID userId, Boolean mandatory);

  UserPreferenceDto getUserPreference(UUID userId, String key);

  List<PreferenceDto> getPreferences(Boolean mandatory);
}
