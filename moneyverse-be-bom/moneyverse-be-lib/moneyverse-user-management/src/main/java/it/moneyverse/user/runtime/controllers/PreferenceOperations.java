package it.moneyverse.user.runtime.controllers;

import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface PreferenceOperations {

  List<UserPreferenceDto> createUserPreferences(
      UUID userId, List<@Valid UserPreferenceRequest> request);

  List<UserPreferenceDto> getUserPreferences(UUID userId, Boolean mandatory);

  UserPreferenceDto getUserPreference(UUID userId, String key);

  List<PreferenceDto> getPreferences(Boolean mandatory);
}
