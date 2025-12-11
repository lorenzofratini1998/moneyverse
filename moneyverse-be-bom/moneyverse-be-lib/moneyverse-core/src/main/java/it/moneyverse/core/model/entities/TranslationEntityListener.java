package it.moneyverse.core.model.entities;

import it.moneyverse.core.model.context.LocaleContextHolder;
import jakarta.persistence.PostLoad;

public class TranslationEntityListener {
  @PostLoad
  public void postLoad(Object entity) {
    if (entity instanceof Translatable translatable) {
      String locale = LocaleContextHolder.getLocale();
      translatable.applyTranslations(locale);
    }
  }
}
