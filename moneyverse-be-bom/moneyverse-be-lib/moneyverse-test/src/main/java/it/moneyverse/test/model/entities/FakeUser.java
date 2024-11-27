package it.moneyverse.test.model.entities;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.util.UUID;

public class FakeUser extends FakeAuditable implements UserModel {

  private final UUID userId;
  private final String name;
  private final String surname;
  private final String email;
  private final String username;
  private final String password;
  private final UserRoleEnum role;

  public FakeUser(Integer counter) {
    counter = counter + 1;
    this.userId = RandomUtils.randomUUID();
    this.name = "Test %s".formatted(counter);
    this.surname = "Test %s".formatted(counter);
    this.email = "test%s@example.com".formatted(counter);
    this.username = "test%s@example.com".formatted(counter);
    this.password = RandomUtils.randomUUID().toString();
    this.role = counter == 1 ? UserRoleEnum.ADMIN : UserRoleEnum.USER;
  }

  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSurname() {
    return surname;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public UserRoleEnum getRole() {
    return role;
  }
}
