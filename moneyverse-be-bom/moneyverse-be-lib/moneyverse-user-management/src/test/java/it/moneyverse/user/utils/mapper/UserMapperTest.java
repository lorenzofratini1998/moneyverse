package it.moneyverse.user.utils.mapper;

import static it.moneyverse.user.utils.UserTestUtils.createUser;
import static it.moneyverse.user.utils.UserTestUtils.createUserUpdateRequest;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.user.model.dto.UserDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
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

  @Test
  void testPartialUpdate_NullUserRepresentation() {
    assertNull(UserMapper.partialUpdate(null, null));
  }

  @Test
  void testPartialUpdate_NullUserUpdateRequestDto() {
    UserRepresentation user = createUser();

    UserRepresentation result = UserMapper.partialUpdate(user, null);

    assertEquals(user, result);
  }

  @Test
  void testPartialUpdate_UserRepresentation() {
    UserRepresentation user = createUser();
    UserUpdateRequestDto request = createUserUpdateRequest();

    UserRepresentation result = UserMapper.partialUpdate(user, request);

    assertEquals(request.firstName(), result.getFirstName());
    assertEquals(request.lastName(), result.getLastName());
    assertEquals(request.email(), result.getEmail());
  }
}
