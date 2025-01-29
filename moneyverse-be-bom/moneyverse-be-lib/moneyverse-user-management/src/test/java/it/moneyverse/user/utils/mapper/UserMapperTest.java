package it.moneyverse.user.utils.mapper;

import static it.moneyverse.user.utils.UserTestUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.user.model.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

class UserMapperTest {

  @Test
  void testToUserDto_NullUserRepresentation() {
    assertNull(UserMapper.toUserDto(null));
  }

  @Test
  void testToUserDto_UserRepresentation() {
    UserRepresentation user = createUser();

    UserDto userDto = UserMapper.toUserDto(user);

    assertEquals(user.getId(), userDto.getUserId().toString());
    assertEquals(user.getFirstName(), userDto.getFirstName());
    assertEquals(user.getLastName(), userDto.getLastName());
    assertEquals(user.getEmail(), userDto.getEmail());
  }
}
