package it.moneyverse.user.services;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {
  PreferenceDto createPreferences(UUID userId, List<PreferenceRequest> request);
}
