package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.model.entities.Currency;
import java.util.Collections;
import java.util.List;

public class CurrencyMapper {

  public static List<CurrencyDto> toCurrencyDto(List<Currency> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(CurrencyMapper::toCurrencyDto).toList();
  }

  public static CurrencyDto toCurrencyDto(Currency currency) {
    if (currency == null) {
      return null;
    }
    return CurrencyDto.builder()
        .withCurrencyId(currency.getCurrencyId())
        .withName(currency.getName())
        .withCode(currency.getCode())
        .withCountry(currency.getCountry())
        .withDefault(currency.isDefault())
        .withEnabled(currency.isEnabled())
        .build();
  }

  private CurrencyMapper() {}
}
