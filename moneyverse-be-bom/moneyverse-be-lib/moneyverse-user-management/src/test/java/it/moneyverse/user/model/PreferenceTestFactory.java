package it.moneyverse.user.model;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PreferenceTestFactory {

  public static List<Preference> createPreferences() {
    return IntStream.range(
            0, RandomUtils.randomInteger(TestFactory.MIN_PREFERENCES, TestFactory.MAX_PREFERENCES))
        .mapToObj(i -> fakePreference())
        .toList();
  }

  public static Preference fakePreference() {
    Preference preference = new Preference();
    preference.setPreferenceId(RandomUtils.randomUUID());
    preference.setName(RandomUtils.randomString(10));
    preference.setDefaultValue(RandomUtils.flipCoin() ? RandomUtils.randomString(5) : null);
    preference.setMandatory(RandomUtils.randomBoolean());
    preference.setUpdatable(RandomUtils.randomBoolean());
    return preference;
  }

  public static List<UserPreference> createUserPreferences(
      List<UserModel> users, List<Preference> preferences) {
    return users.stream()
        .map(
            user -> {
              List<Preference> mandatoryPreferences =
                  preferences.stream().filter(Preference::getMandatory).toList();
              List<Preference> nonMandatoryPreferences =
                  preferences.stream().filter(p -> !p.getMandatory()).collect(Collectors.toList());
              return createUserPreference(user, mandatoryPreferences, nonMandatoryPreferences);
            })
        .flatMap(List::stream)
        .toList();
  }

  private static List<UserPreference> createUserPreference(
      UserModel user,
      List<Preference> mandatoryPreferences,
      List<Preference> nonMandatoryPreferences) {
    List<UserPreference> userPreferences = new ArrayList<>();
    for (Preference preference : mandatoryPreferences) {
      userPreferences.add(fakeUserPreference(user.getUserId(), preference));
    }
    Collections.shuffle(nonMandatoryPreferences);
    for (int i = 0; i < RandomUtils.randomInteger(0, nonMandatoryPreferences.size()); i++) {
      userPreferences.add(fakeUserPreference(user.getUserId(), nonMandatoryPreferences.get(i)));
    }
    return userPreferences;
  }

  public static UserPreference fakeUserPreference(UUID userId, Preference preference) {
    UserPreference userPreference = new UserPreference();
    userPreference.setUserId(userId);
    userPreference.setUserPreferenceId(RandomUtils.randomUUID());
    userPreference.setValue(RandomUtils.randomString(10));
    userPreference.setPreference(preference);
    userPreference.setCreatedBy(TestFactory.FAKE_USER);
    userPreference.setCreatedAt(LocalDateTime.now());
    userPreference.setUpdatedBy(TestFactory.FAKE_USER);
    userPreference.setUpdatedAt(LocalDateTime.now());
    return userPreference;
  }

  public static class UserPreferenceRequestBuilder {
    private UUID preferenceId = RandomUtils.randomUUID();
    private String value = RandomUtils.randomString(10);

    public static Stream<Supplier<List<UserPreferenceRequest>>>
        invalidPreferencesRequestProvider() {
      return Stream.of(
          () -> Collections.singletonList(builder().withNullPreferenceId().build()),
          () -> Collections.singletonList(builder().withNullValue().build()));
    }

    private UserPreferenceRequestBuilder withNullPreferenceId() {
      this.preferenceId = null;
      return this;
    }

    private UserPreferenceRequestBuilder withNullValue() {
      this.value = null;
      return this;
    }

    public UserPreferenceRequestBuilder withPreferenceId(UUID preferenceId) {
      this.preferenceId = preferenceId;
      return this;
    }

    public static UserPreferenceRequest defaultInstance() {
      return builder().build();
    }

    public static UserPreferenceRequestBuilder builder() {
      return new UserPreferenceRequestBuilder();
    }

    public UserPreferenceRequest build() {
      return new UserPreferenceRequest(preferenceId, value);
    }
  }

  private PreferenceTestFactory() {}
}
