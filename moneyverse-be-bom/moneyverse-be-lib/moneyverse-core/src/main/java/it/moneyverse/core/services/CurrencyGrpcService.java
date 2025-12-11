package it.moneyverse.core.services;

import static it.moneyverse.core.utils.constants.CacheConstants.CURRENCIES_CACHE;
import static it.moneyverse.core.utils.constants.CacheConstants.RATES_CACHE;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.core.model.dto.ExchangeRateDto;
import it.moneyverse.core.utils.properties.CurrencyServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CurrencyGrpcService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyGrpcService.class);
  private final CurrencyServiceGrpc.CurrencyServiceBlockingStub stub;

  public CurrencyGrpcService(CurrencyServiceGrpc.CurrencyServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Cacheable(value = CURRENCIES_CACHE, key = "#code", unless = "#result == null")
  @CircuitBreaker(
      name = CurrencyServiceGrpcCircuitBreakerProperties.CURRENCY_SERVICE_GRPC,
      fallbackMethod = "fallbackGetCurrencyByCode")
  public Optional<CurrencyDto> getCurrencyByCode(String code) {
    final CurrencyResponse response =
        stub.getCurrencyByCode(CurrencyRequest.newBuilder().setIsoCode(code).build());
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        CurrencyDto.builder()
            .withCurrencyId(UUID.fromString(response.getCurrencyId()))
            .withCode(response.getIsoCode())
            .build());
  }

  @Cacheable(
      value = RATES_CACHE,
      key = "#currencyFrom + '_' + #currencyTo + '_' + #date.toString()",
      unless = "#result == null")
  @CircuitBreaker(
      name = CurrencyServiceGrpcCircuitBreakerProperties.CURRENCY_SERVICE_GRPC,
      fallbackMethod = "fallbackGetExchangeRate")
  public Optional<ExchangeRateDto> getExchangeRate(
      String currencyFrom, String currencyTo, LocalDate date) {
    if (currencyFrom.equals(currencyTo)) {
      return Optional.of(
          ExchangeRateDto.builder()
              .withCurrencyFrom(currencyFrom)
              .withCurrencyTo(currencyTo)
              .withDate(date)
              .withRate(BigDecimal.ONE)
              .build());
    }
    final ExchangeRateResponse response =
        stub.getExchangeRate(
            ExchangeRateRequest.newBuilder()
                .setFromCurrency(currencyFrom)
                .setToCurrency(currencyTo)
                .setDate(date.toString())
                .build());
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        ExchangeRateDto.builder()
            .withCurrencyFrom(currencyFrom)
            .withCurrencyTo(currencyTo)
            .withDate(date)
            .withRate(BigDecimal.valueOf(response.getRate()))
            .build());
  }

  private boolean isEmptyResponse(CurrencyResponse response) {
    return response.getCurrencyId().isEmpty() && response.getIsoCode().isEmpty();
  }

  private boolean isEmptyResponse(ExchangeRateResponse response) {
    return response.getRate() == 0;
  }

  protected Optional<CurrencyDto> fallbackGetCurrencyByCode(String code, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the CurrencyService to retrieve the currency {}. Returning FALSE as fallback: {}",
        code,
        throwable.getMessage());
    return Optional.empty();
  }

  protected Optional<ExchangeRateDto> fallbackGetExchangeRate(
      String currencyFrom, String currencyTo, LocalDate date, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the CurrencyService to retrieve the exchange rate between {} and {} for the date {}.. Returning FALSE as fallback: {}",
        currencyFrom,
        currencyTo,
        date,
        throwable.getMessage());
    return Optional.empty();
  }
}
