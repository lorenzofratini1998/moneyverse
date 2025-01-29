package it.moneyverse.user.services;

import it.moneyverse.user.model.dto.UserDto;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {

  Optional<UserDto> getUserById(UUID userId);

  Optional<String> getUserAttributeValue(UUID userId, String key);

  void setUserAttribute(UUID userId, String key, String value);
}
