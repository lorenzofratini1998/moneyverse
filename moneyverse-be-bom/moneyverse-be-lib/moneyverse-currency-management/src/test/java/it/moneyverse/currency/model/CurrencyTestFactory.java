package it.moneyverse.currency.model;

import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurrencyTestFactory {

  public static List<Currency> createCurrencies() {
    int currenciesNumber = RandomUtils.randomInteger(3, 30);
    List<Currency> currencies = new ArrayList<>();
    for (int i = 0; i < currenciesNumber; i++) {
      currencies.add(fakeCurrency());
    }
    return currencies;
  }

  public static Currency fakeCurrency() {
    Currency currency = new Currency();
    currency.setCurrencyId(RandomUtils.randomUUID());
    currency.setCode(RandomUtils.randomCurrency());
    currency.setCountry(RandomUtils.randomString(20));
    currency.setName(RandomUtils.randomString(20));
    currency.setDefault(RandomUtils.randomBoolean());
    currency.setEnabled(RandomUtils.randomBoolean());
    return currency;
  }

  public static ExchangeRate fakeExchangeRate() {
    ExchangeRate exchangeRate = new ExchangeRate();
    exchangeRate.setDate(LocalDate.now());
    exchangeRate.setCurrencyFrom(RandomUtils.randomCurrency());
    exchangeRate.setCurrencyTo(RandomUtils.randomCurrency());
    exchangeRate.setRate(RandomUtils.randomBigDecimal());
    return exchangeRate;
  }

  private CurrencyTestFactory() {}
}
