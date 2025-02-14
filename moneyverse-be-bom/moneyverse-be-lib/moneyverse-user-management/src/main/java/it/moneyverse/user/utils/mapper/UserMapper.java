package it.moneyverse.user.utils.mapper;

import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import java.util.UUID;
import org.keycloak.representations.idm.UserRepresentation;

public class UserMapper {

  public static UserDto toUserDto(UserRepresentation user) {
    if (user == null) {
      return null;
    }
    return UserDto.builder()
        .withUserId(UUID.fromString(user.getId()))
        .withFirstName(user.getFirstName())
        .withLastName(user.getLastName())
        .withEmail(user.getEmail())
        .build();
  }

  public static UserRepresentation partialUpdate(
      UserRepresentation user, UserUpdateRequestDto request) {
    if (user == null) {
      return null;
    }
    if (request == null) {
      return user;
    }
    if (request.firstName() != null) {
      user.setFirstName(request.firstName());
    }
    if (request.lastName() != null) {
      user.setLastName(request.lastName());
    }
    if (request.email() != null) {
      user.setEmail(request.email());
      user.setEmailVerified(false);
    }
    return user;
  }

  private UserMapper() {}
}
