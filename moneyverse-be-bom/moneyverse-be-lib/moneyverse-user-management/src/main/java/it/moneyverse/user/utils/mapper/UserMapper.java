package it.moneyverse.user.utils.mapper;

import it.moneyverse.user.model.dto.UserDto;
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

  private UserMapper() {}
}
