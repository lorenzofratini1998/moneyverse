package it.moneyverse.test.model.entities;

import static it.moneyverse.test.model.TestFactory.ONBOARD;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeUser extends FakeAuditable implements UserModel {

  private UUID userId;
  private final String name;
  private final String surname;
  private final String email;
  private final String username;
  private final String password;
  private final UserRoleEnum role;
  private final Map<String, String> attributes;

  public FakeUser(Integer counter) {
    counter = counter + 1;
    this.userId = RandomUtils.randomUUID();
    this.name = "Test %s".formatted(counter);
    this.surname = "Test %s".formatted(counter);
    this.email = "test%s@example.com".formatted(counter);
    this.username = "test%s@example.com".formatted(counter);
    this.password = RandomUtils.randomString(20);
    this.role = counter == 1 ? UserRoleEnum.ADMIN : UserRoleEnum.USER;
    this.attributes =
        new HashMap<>() {
          {
            put(ONBOARD, RandomUtils.randomBoolean().toString());
          }
        };
  }

  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public void setUserId(UUID userId) {
    this.userId = userId;
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

  @Override
  public Map<String, String> getAttributes() {
    return attributes;
  }
}
