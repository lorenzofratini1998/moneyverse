package it.moneyverse.core.services;

import it.moneyverse.core.model.dto.UserDto;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceClient {

  Optional<UserDto> getUserById(UUID userId);

  void checkIfUserStillExist(UUID userId);
}
