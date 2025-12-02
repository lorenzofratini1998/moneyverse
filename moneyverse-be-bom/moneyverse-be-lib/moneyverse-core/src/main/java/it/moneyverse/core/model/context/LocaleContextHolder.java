package it.moneyverse.core.model.context;

public class LocaleContextHolder {
  private static final ThreadLocal<String> localeContext = new ThreadLocal<>();
  private static final String DEFAULT_LOCALE = "en";

  public static void setLocale(String locale) {
    localeContext.set(locale);
  }

  public static String getLocale() {
    String locale = localeContext.get();
    return locale != null ? locale : DEFAULT_LOCALE;
  }

  public static void clear() {
    localeContext.remove();
  }

}
