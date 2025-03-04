package it.moneyverse.user.model;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import java.util.function.Supplier;
import org.keycloak.representations.idm.UserRepresentation;

public class UserTestFactory {

  private static final Supplier<String> FAKE_FIRST_NAME = () -> RandomUtils.randomString(10);
  private static final Supplier<String> FAKE_LAST_NAME = () -> RandomUtils.randomString(10);
  private static final Supplier<String> FAKE_EMAIL =
      () ->
          "%s@%s.%s"
              .formatted(
                  RandomUtils.randomString(10),
                  RandomUtils.randomString(5),
                  RandomUtils.randomString(2));

  public static UserRepresentation fakeUser() {
    UserRepresentation user = new UserRepresentation();
    user.setId(RandomUtils.randomUUID().toString());
    user.setFirstName(FAKE_FIRST_NAME.get());
    user.setLastName(FAKE_LAST_NAME.get());
    user.setEmail(FAKE_EMAIL.get());
    return user;
  }

  public static class UserUpdateRequestBuilder {
    private final String firstName = FAKE_FIRST_NAME.get();
    private final String lastName = FAKE_LAST_NAME.get();
    private String email = FAKE_EMAIL.get();

    public UserUpdateRequestBuilder withNullEmail() {
      this.email = null;
      return this;
    }

    public UserUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public static UserUpdateRequestBuilder builder() {
      return new UserUpdateRequestBuilder();
    }

    public UserUpdateRequestDto build() {
      return new UserUpdateRequestDto(firstName, lastName, email);
    }
  }

  private UserTestFactory() {}
}
