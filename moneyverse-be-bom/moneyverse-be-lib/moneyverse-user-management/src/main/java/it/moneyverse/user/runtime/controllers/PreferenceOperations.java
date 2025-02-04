package it.moneyverse.user.runtime.controllers;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface PreferenceOperations {

  UserPreferenceDto createUserPreferences(UUID userId, List<@Valid UserPreferenceRequest> request);

  UserPreferenceDto getUserPreferences(UUID userId, Boolean mandatory);

  List<PreferenceDto> getPreferences(Boolean mandatory);
}
