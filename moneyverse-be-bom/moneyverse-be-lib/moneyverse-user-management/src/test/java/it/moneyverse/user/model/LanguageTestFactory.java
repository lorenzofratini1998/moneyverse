package it.moneyverse.user.model;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.entities.Language;
import java.util.List;
import java.util.stream.IntStream;

public class LanguageTestFactory {

  public static List<Language> createLanguages() {
    return IntStream.range(2, 5).mapToObj(LanguageTestFactory::fakeLanguage).toList();
  }

  public static Language fakeLanguage(int i) {
    Language language = new Language();
    language.setLanguageId(RandomUtils.randomUUID());
    language.setIsoCode(RandomUtils.randomString(5).toUpperCase());
    language.setCountry(RandomUtils.randomString(20));
    language.setIcon(RandomUtils.randomString(10));
    language.setDefault(i == 0);
    return language;
  }

  private LanguageTestFactory() {}
}
