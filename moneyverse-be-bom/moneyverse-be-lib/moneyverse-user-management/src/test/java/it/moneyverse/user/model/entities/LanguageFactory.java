package it.moneyverse.user.model.entities;

import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;

public class LanguageFactory {

  public static List<Language> createLanguages() {
    List<Language> languages = new ArrayList<Language>();
    for (int i = 0; i < RandomUtils.randomInteger(2, 5); i++) {
      Language language = new Language();
      language.setLanguageId(RandomUtils.randomUUID());
      language.setIsoCode(RandomUtils.randomString(3).toUpperCase());
      language.setCountry(RandomUtils.randomString(20));
      language.setEnabled(true);
      language.setDefault(i == 0);
      languages.add(language);
    }
    return languages;
  }

  private LanguageFactory() {}
}
