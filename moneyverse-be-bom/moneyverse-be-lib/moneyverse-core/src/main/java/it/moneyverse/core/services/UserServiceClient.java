package it.moneyverse.core.services;

import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceClient {

  Optional<UserDto> getUserById(UUID userId);

  Optional<UserPreferenceDto> getUserPreference(UUID userId, String preferenceName);

  void checkIfUserStillExist(UUID userId);
}
