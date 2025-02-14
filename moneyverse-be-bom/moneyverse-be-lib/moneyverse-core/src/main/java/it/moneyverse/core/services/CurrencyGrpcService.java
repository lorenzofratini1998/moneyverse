package it.moneyverse.core.services;

import static it.moneyverse.core.utils.constants.CacheConstants.CURRENCIES_CACHE;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.core.utils.properties.CurrencyServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.CurrencyRequest;
import it.moneyverse.grpc.lib.CurrencyResponse;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
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

  private boolean isEmptyResponse(CurrencyResponse response) {
    return response.getCurrencyId().isEmpty() && response.getIsoCode().isEmpty();
  }

  protected Optional<CurrencyDto> fallbackGetCurrencyByCode(String code, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the CurrencyService to retrieve whether the currency {}. Returning FALSE as fallback: {}",
        code,
        throwable.getMessage());
    return Optional.empty();
  }
}
