package it.moneyverse.currency.model;

import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;

public class CurrencyFactory {

  public static List<Currency> createCurrencies() {
    int currenciesNumber = RandomUtils.randomInteger(3, 30);
    List<Currency> currencies = new ArrayList<>();
    for (int i = 0; i < currenciesNumber; i++) {
      currencies.add(fakeCurrency());
    }
    return currencies;
  }

  private static Currency fakeCurrency() {
    Currency currency = new Currency();
    currency.setCurrencyId(RandomUtils.randomUUID());
    currency.setCode(RandomUtils.randomString(3).toUpperCase());
    currency.setCountry(RandomUtils.randomString(20));
    currency.setName(RandomUtils.randomString(20));
    currency.setDefault(RandomUtils.randomBoolean());
    currency.setEnabled(RandomUtils.randomBoolean());
    return currency;
  }

  private CurrencyFactory() {}
}
