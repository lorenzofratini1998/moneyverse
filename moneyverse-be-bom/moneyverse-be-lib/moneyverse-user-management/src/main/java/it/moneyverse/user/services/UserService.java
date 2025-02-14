package it.moneyverse.user.services;

import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import java.util.UUID;

public interface UserService {

  UserDto getUser(UUID userId);

  UserDto updateUser(UUID userId, UserUpdateRequestDto request);

  void disableUser(UUID userId);

  void deleteUser(UUID userId);
}
