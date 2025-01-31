package it.moneyverse.user.services;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {
  UserPreferenceDto createUserPreferences(UUID userId, List<UserPreferenceRequest> request);

  UserPreferenceDto getUserPreferences(UUID userId, Boolean mandatory);

  List<PreferenceDto> getPreferences(Boolean mandatory);
}
