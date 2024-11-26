package it.moneyverse.test.model;

import it.moneyverse.enums.UserRoleEnum;
import it.moneyverse.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.util.UUID;

public class FakeUser implements UserModel {

  private final Integer counter;

  public FakeUser(Integer counter) {
    this.counter = counter + 1;
  }

  @Override
  public UUID getUserId() {
    return RandomUtils.randomUUID();
  }

  @Override
  public String getName() {
    return "Test %s".formatted(counter);
  }

  @Override
  public String getSurname() {
    return "Test %s".formatted(counter);
  }

  @Override
  public String getEmail() {
    return "test%s@example.com".formatted(counter);
  }

  @Override
  public String getUsername() {
    return "test%s@example.com".formatted(counter);
  }

  @Override
  public String getPassword() {
    return RandomUtils.randomUUID().toString();
  }

  @Override
  public UserRoleEnum getRole() {
    return counter == 0 ? UserRoleEnum.ADMIN : UserRoleEnum.USER;
  }
}
