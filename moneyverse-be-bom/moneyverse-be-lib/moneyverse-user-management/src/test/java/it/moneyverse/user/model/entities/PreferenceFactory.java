package it.moneyverse.user.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PreferenceFactory {

  public static List<Preference> createPreferences() {
    List<Preference> preferences = new ArrayList<>();
    for (int i = 0; i < RandomUtils.randomInteger(MIN_PREFERENCES, MAX_PREFERENCES); i++) {
      Preference preference = new Preference();
      preference.setPreferenceId(RandomUtils.randomUUID());
      preference.setName(RandomUtils.randomString(10));
      preference.setDefaultValue(Math.random() < 0.5 ? RandomUtils.randomString(5) : null);
      preference.setMandatory(RandomUtils.randomBoolean());
      preference.setUpdatable(RandomUtils.randomBoolean());
      preferences.add(preference);
    }
    return preferences;
  }

  public static List<UserPreference> createUserPreferences(
      List<UserModel> users, List<Preference> preferences) {
    List<UserPreference> userPreferences = new ArrayList<>();
    List<Preference> mandatoryPreferences =
        preferences.stream().filter(Preference::getMandatory).toList();
    List<Preference> nonMandatoryPreferences =
        preferences.stream().filter(p -> !p.getMandatory()).collect(Collectors.toList());
    for (UserModel user : users) {
      for (Preference preference : mandatoryPreferences) {
        userPreferences.add(createUserPreference(user.getUserId(), preference));
      }
      Collections.shuffle(nonMandatoryPreferences);
      for (int i = 0; i < RandomUtils.randomInteger(0, nonMandatoryPreferences.size()); i++) {
        userPreferences.add(createUserPreference(user.getUserId(), nonMandatoryPreferences.get(i)));
      }
    }
    return userPreferences;
  }

  private static UserPreference createUserPreference(UUID userId, Preference preference) {
    UserPreference userPreference = new UserPreference();
    userPreference.setUserId(userId);
    userPreference.setUserPreferenceId(RandomUtils.randomUUID());
    userPreference.setValue(RandomUtils.randomString(10));
    userPreference.setPreference(preference);
    userPreference.setCreatedBy(FAKE_USER);
    userPreference.setCreatedAt(LocalDateTime.now());
    userPreference.setUpdatedBy(FAKE_USER);
    userPreference.setUpdatedAt(LocalDateTime.now());
    return userPreference;
  }

  private PreferenceFactory() {}
}
