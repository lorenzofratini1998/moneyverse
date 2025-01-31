package it.moneyverse.user.utils;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.keycloak.representations.idm.UserRepresentation;

public class UserTestUtils {

  public static UserRepresentation createUser() {
    UserRepresentation user = new UserRepresentation();
    user.setId(RandomUtils.randomUUID().toString());
    user.setFirstName(randomFirstName());
    user.setLastName(randomLastName());
    user.setEmail(randomEmail());
    return user;
  }

  private static String randomFirstName() {
    return RandomUtils.randomString(10);
  }

  private static String randomLastName() {
    return RandomUtils.randomString(10);
  }

  private static String randomEmail() {
    return "%s@%s.%s"
        .formatted(
            RandomUtils.randomString(10), RandomUtils.randomString(5), RandomUtils.randomString(2));
  }

  public static UserPreference createUserPreference(UUID userId) {
    UserPreference preference = new UserPreference();
    preference.setUserId(userId);
    preference.setUserPreferenceId(RandomUtils.randomUUID());
    preference.setValue(RandomUtils.randomString(10));
    preference.setPreference(createPreference());
    return preference;
  }

  public static Preference createPreference() {
    Preference preference = new Preference();
    preference.setPreferenceId(RandomUtils.randomUUID());
    preference.setName(RandomUtils.randomString(10));
    preference.setDefaultValue(RandomUtils.randomString(10));
    preference.setMandatory(RandomUtils.randomBoolean());
    preference.setUpdatable(RandomUtils.randomBoolean());
    return preference;
  }

  public static UserPreferenceRequest createUserPreferenceRequest(UUID preferenceId) {
    return new UserPreferenceRequest(preferenceId, RandomUtils.randomString(10));
  }

  public static Stream<Supplier<List<UserPreferenceRequest>>> invalidPreferencesRequestProvider() {
    return Stream.of(
        UserTestUtils::createPreferenceRequestWithNullKey,
        UserTestUtils::createPreferenceRequestWithNullValue);
  }

  private static List<UserPreferenceRequest> createPreferenceRequestWithNullKey() {
    return Collections.singletonList(new UserPreferenceRequest(null, RandomUtils.randomString(10)));
  }

  private static List<UserPreferenceRequest> createPreferenceRequestWithNullValue() {
    return Collections.singletonList(new UserPreferenceRequest(RandomUtils.randomUUID(), null));
  }

  private UserTestUtils() {}
}
