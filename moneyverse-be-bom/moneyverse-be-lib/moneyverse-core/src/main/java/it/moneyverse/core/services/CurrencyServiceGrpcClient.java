package it.moneyverse.core.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.core.model.dto.ExchangeRateDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceGrpcClient implements CurrencyServiceClient {

  private static final String CURRENCY = "CURRENCY";

  private final CurrencyGrpcService currencyGrpcService;
  private final UserServiceClient userServiceClient;

  public CurrencyServiceGrpcClient(
      CurrencyGrpcService currencyGrpcService,
      @Autowired(required = false) UserServiceClient userServiceClient) {
    this.currencyGrpcService = currencyGrpcService;
    this.userServiceClient = userServiceClient;
  }

  @Override
  public Optional<CurrencyDto> getCurrencyByCode(String code) {
    return currencyGrpcService.getCurrencyByCode(code);
  }

  @Override
  public Optional<ExchangeRateDto> getExchangeRate(
      String currencyFrom, String currencyTo, LocalDate date) {
    return currencyGrpcService.getExchangeRate(currencyFrom, currencyTo, date);
  }

  @Override
  public void checkIfCurrencyExists(String currency) {
    if (currencyGrpcService.getCurrencyByCode(currency).isEmpty()) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }

  @Override
  public BigDecimal convertCurrencyAmountByUserPreference(
      UUID userId, BigDecimal amount, String currency, LocalDate date) {
    if (currency == null) {
      return amount;
    }
    return userServiceClient
        .getUserPreference(userId, CURRENCY)
        .filter(userCurrency -> !userCurrency.getValue().equals(currency))
        .map(userCurrency -> convertAmount(amount, currency, userCurrency.getValue(), date))
        .orElse(amount);
  }

  @Override
  public BigDecimal convertAmount(
      BigDecimal amount, String currencyFrom, String currencyTo, LocalDate date) {
    Optional<ExchangeRateDto> exchangeRate = getExchangeRate(currencyFrom, currencyTo, date);
    return exchangeRate
        .map(ExchangeRateDto::getRate)
        .map(rate -> rate.multiply(amount))
        .orElse(amount);
  }
}
