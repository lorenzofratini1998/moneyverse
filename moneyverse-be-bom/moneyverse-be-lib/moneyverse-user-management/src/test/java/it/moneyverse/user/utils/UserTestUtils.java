package it.moneyverse.user.utils;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.enums.PreferenceKeyEnum;
import it.moneyverse.user.model.dto.PreferenceRequest;
import java.util.Arrays;
import java.util.List;
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

  public static List<PreferenceRequest> createPreferencesRequest() {
    return Arrays.stream(PreferenceKeyEnum.values())
        .map(
            preferenceKeyEnum ->
                new PreferenceRequest(preferenceKeyEnum, RandomUtils.randomString(10)))
        .toList();
  }

  public static Stream<Supplier<List<PreferenceRequest>>> invalidPreferencesRequestProvider() {
    return Stream.of(
        UserTestUtils::createPreferenceRequestWithNullKey,
        UserTestUtils::createPreferenceRequestWithNullValue);
  }

  private static List<PreferenceRequest> createPreferenceRequestWithNullKey() {
    return Arrays.stream(PreferenceKeyEnum.values())
        .map(preferenceKeyEnum -> new PreferenceRequest(null, RandomUtils.randomString(10)))
        .toList();
  }

  private static List<PreferenceRequest> createPreferenceRequestWithNullValue() {
    return Arrays.stream(PreferenceKeyEnum.values())
        .map(preferenceKeyEnum -> new PreferenceRequest(preferenceKeyEnum, null))
        .toList();
  }

  private UserTestUtils() {}
}
