package it.moneyverse.user.model;

import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.entities.*;
import java.nio.file.Path;
import java.util.*;

public class UserTestContext extends TestContext<UserTestContext> {

  private static UserTestContext currencyTestContext;
  private final List<Language> languages;
  private final List<Preference> preferences;
  private final List<UserPreference> userPreferences;

  public UserTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    languages = LanguageTestFactory.createLanguages();
    preferences = PreferenceTestFactory.createPreferences();
    userPreferences = PreferenceTestFactory.createUserPreferences(getUsers(), preferences);
    setCurrentInstance(this);
  }

  private static void setCurrentInstance(UserTestContext instance) {
    currencyTestContext = instance;
  }

  private static UserTestContext getCurrentInstance() {
    if (currencyTestContext == null) {
      throw new IllegalStateException("UserTestContext not initialized");
    }
    return currencyTestContext;
  }

  public List<Language> getLanguages() {
    return languages;
  }

  public List<Preference> getPreferences() {
    return preferences;
  }

  public List<UserPreference> getUserPreferences() {
    return userPreferences;
  }

  public List<UserPreference> getMandatoryUserPreferencesByUserId(UUID userId) {
    return getUserPreferencesByUserId(userId).stream()
        .filter(userPreference -> userPreference.getPreference().getMandatory().equals(true))
        .toList();
  }

  public List<UserPreference> getUserPreferencesByUserId(UUID userId) {
    return getUserPreferences().stream()
        .filter(preference -> preference.getUserId().equals(userId))
        .toList();
  }

  public List<Preference> getMandatoryPreferences() {
    return preferences.stream().filter(Preference::getMandatory).toList();
  }

  public UserPreference getRandomUserPreference(UUID userId) {
    List<UserPreference> userPreferences = getUserPreferencesByUserId(userId);
    return userPreferences.get(RandomUtils.randomInteger(userPreferences.size()));
  }

  @Override
  public UserTestContext self() {
    return this;
  }

  @Override
  public UserTestContext generateScript(Path dir) {
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, languages, preferences, userPreferences),
            new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }
}
