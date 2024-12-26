package it.moneyverse.core.enums;

public enum CurrencyEnum {
  USD("United States Dollar", "United States"),
  EUR("Euro", "European Union"),
  GBP("Pound Sterling", "United Kingdom"),
  CHF("Swiss Franc", "Switzerland");

  private final String currencyName;
  private final String country;

  CurrencyEnum(String currencyName, String country) {
    this.currencyName = currencyName;
    this.country = country;
  }

  public String currencyCode() {
    return name();
  }

  public String currencyName() {
    return currencyName;
  }

  public String country() {
    return country;
  }
}
