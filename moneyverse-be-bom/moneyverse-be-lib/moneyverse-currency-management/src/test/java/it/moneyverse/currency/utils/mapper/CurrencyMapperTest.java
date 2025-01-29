package it.moneyverse.currency.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.utils.CurrencyMapper;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CurrencyMapperTest {

  @Test
  void testToCurrencyDto_NullCurrencyEntity() {
    assertNull(CurrencyMapper.toCurrencyDto((Currency) null));
  }

  @Test
  void testToCurrencyDto_ValidCurrencyEntity() {
    Currency currency = createCurrency();

    CurrencyDto result = CurrencyMapper.toCurrencyDto(currency);

    assertEquals(currency.getCurrencyId(), result.getCurrencyId());
    assertEquals(currency.getCode(), result.getCode());
    assertEquals(currency.getName(), result.getName());
    assertEquals(currency.getCountry(), result.getCountry());
  }

  @Test
  void testToCurrencyDto_EmptyEntityList() {
    assertEquals(Collections.emptyList(), CurrencyMapper.toCurrencyDto(new ArrayList<>()));
  }

  @Test
  void testToCurrencyDto_NonEmptyEntityList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<Currency> currencies = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      currencies.add(createCurrency());
    }

    List<CurrencyDto> currencyDtos = CurrencyMapper.toCurrencyDto(currencies);

    for (int i = 0; i < entitiesCount; i++) {
      Currency currency = currencies.get(i);
      CurrencyDto currencyDto = currencyDtos.get(i);

      assertEquals(currency.getCurrencyId(), currencyDto.getCurrencyId());
      assertEquals(currency.getCode(), currencyDto.getCode());
      assertEquals(currency.getName(), currencyDto.getName());
      assertEquals(currency.getCountry(), currencyDto.getCountry());
    }
  }

  private Currency createCurrency() {
    Currency currency = new Currency();
    currency.setCurrencyId(RandomUtils.randomUUID());
    currency.setName(RandomUtils.randomString(15));
    currency.setCode(RandomUtils.randomString(3));
    currency.setCountry(RandomUtils.randomString(15));
    return currency;
  }
}
