package it.moneyverse.core.services;

import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.core.model.dto.ExchangeRateDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyServiceClient {

  Optional<CurrencyDto> getCurrencyByCode(String code);

  Optional<ExchangeRateDto> getExchangeRate(String currencyFrom, String currencyTo, LocalDate date);

  void checkIfCurrencyExists(String currency);

  BigDecimal convertCurrencyAmountByUserPreference(
      UUID userId, BigDecimal amount, String currency, LocalDate date);

  BigDecimal convertAmount(
      BigDecimal amount, String currencyFrom, String currencyTo, LocalDate date);
}
