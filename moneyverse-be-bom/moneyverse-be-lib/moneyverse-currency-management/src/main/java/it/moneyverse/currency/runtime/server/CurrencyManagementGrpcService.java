package it.moneyverse.currency.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.grpc.lib.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CurrencyManagementGrpcService extends CurrencyServiceGrpc.CurrencyServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyManagementGrpcService.class);
  private static final String ISO_CODE_EUR = "EUR";
  private final CurrencyRepository currencyRepository;
  private final ExchangeRateRepository exchangeRateRepository;

  public CurrencyManagementGrpcService(
      CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository) {
    this.currencyRepository = currencyRepository;
    this.exchangeRateRepository = exchangeRateRepository;
  }

  @Override
  public void getCurrencyByCode(
      CurrencyRequest request, StreamObserver<CurrencyResponse> responseObserver) {
    Optional<Currency> currency = currencyRepository.findByCode(request.getIsoCode());
    CurrencyResponse response = getCurrencyResponse(request.getIsoCode(), currency);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  public CurrencyResponse getCurrencyResponse(String isoCode, Optional<Currency> currency) {
    if (currency.isEmpty()) {
      LOGGER.error("Currency with ISO_CODE {} does not exists.", isoCode);
      return CurrencyResponse.getDefaultInstance();
    }
    Currency curr = currency.get();
    if (Boolean.FALSE.equals(curr.isEnabled())) {
      LOGGER.warn("Currency with ISO_CODE {} is not enabled.", isoCode);
      return CurrencyResponse.getDefaultInstance();
    }
    return CurrencyResponse.newBuilder()
        .setCurrencyId(curr.getCurrencyId().toString())
        .setIsoCode(curr.getCode())
        .build();
  }

  @Override
  public void getExchangeRate(
      ExchangeRateRequest request, StreamObserver<ExchangeRateResponse> responseObserver) {
    String fromCurrency = request.getFromCurrency();
    String toCurrency = request.getToCurrency();
    LocalDate date = LocalDate.parse(request.getDate());
    ExchangeRateResponse response;
    try {
      if (isSameCurrency(fromCurrency, toCurrency)) {
        response = buildExchangeRateResponse(1.0);
      } else if (request.getFromCurrency().equalsIgnoreCase(ISO_CODE_EUR)) {
        response = getExchangeRateFromEur(toCurrency, date);
      } else if (request.getToCurrency().equalsIgnoreCase(ISO_CODE_EUR)) {
        response = getExchangeRateToEur(fromCurrency, date);
      } else {
        response = getExchangeRate(fromCurrency, toCurrency, date);
      }
    } catch (ResourceNotFoundException e) {
      LOGGER.error(e.getMessage());
      response = ExchangeRateResponse.getDefaultInstance();
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private boolean isSameCurrency(String fromCurrency, String toCurrency) {
    return fromCurrency.equalsIgnoreCase(toCurrency);
  }

  private ExchangeRateResponse getExchangeRateFromEur(String toCurrency, LocalDate date) {
    BigDecimal rate = getRate(ISO_CODE_EUR, toCurrency, date);
    return buildExchangeRateResponse(rate.doubleValue());
  }

  private ExchangeRateResponse getExchangeRateToEur(String fromCurrency, LocalDate date) {
    BigDecimal rate = getRate(fromCurrency, ISO_CODE_EUR, date);
    return buildExchangeRateResponse(
        BigDecimal.ONE.divide(rate, 4, RoundingMode.HALF_UP).doubleValue());
  }

  private ExchangeRateResponse getExchangeRate(
      String fromCurrency, String toCurrency, LocalDate date) {
    BigDecimal rateFromEur = getRate(ISO_CODE_EUR, fromCurrency, date);
    BigDecimal rateToEur = getRate(ISO_CODE_EUR, toCurrency, date);
    return buildExchangeRateResponse(
        rateToEur.divide(rateFromEur, RoundingMode.HALF_UP).doubleValue());
  }

  private BigDecimal getRate(String fromCurrency, String toCurrency, LocalDate date) {
    return exchangeRateRepository
        .findExchangeRateByCurrencyFromAndCurrencyToAndDate(fromCurrency, toCurrency, date)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Exchange rate from %s to %s on %s does not exist."
                        .formatted(fromCurrency, toCurrency, date)))
        .getRate();
  }

  private ExchangeRateResponse buildExchangeRateResponse(double rate) {
    return ExchangeRateResponse.newBuilder().setRate(rate).build();
  }
}
