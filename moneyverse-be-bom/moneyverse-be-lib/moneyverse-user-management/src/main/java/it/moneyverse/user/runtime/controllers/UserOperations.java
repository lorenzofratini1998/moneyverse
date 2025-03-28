package it.moneyverse.user.runtime.controllers;

import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.user.model.dto.LanguageDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserOperations {

  UserDto getUser(UUID userId);

  UserDto updateUser(UUID userId, UserUpdateRequestDto request);

  void disableUser(UUID userId);

  void deleteUser(UUID userId);

  List<LanguageDto> getLanguages();
}
