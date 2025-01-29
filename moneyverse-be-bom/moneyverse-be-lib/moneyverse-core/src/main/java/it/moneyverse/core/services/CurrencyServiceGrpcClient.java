package it.moneyverse.core.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.utils.properties.CurrencyServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.CurrencyRequest;
import it.moneyverse.grpc.lib.CurrencyResponse;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceGrpcClient implements CurrencyServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServiceGrpcClient.class);
  private final CurrencyServiceGrpc.CurrencyServiceBlockingStub stub;

  public CurrencyServiceGrpcClient(CurrencyServiceGrpc.CurrencyServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  @CircuitBreaker(
      name = CurrencyServiceGrpcCircuitBreakerProperties.CURRENCY_SERVICE_GRPC,
      fallbackMethod = "fallbackCheckIfCurrencyExists")
  public Boolean checkIfCurrencyExists(String code) {
    final CurrencyResponse response =
        stub.checkIfCurrencyExists(CurrencyRequest.newBuilder().setCode(code).build());
    return response.getExists();
  }

  protected Boolean fallbackCheckIfCurrencyExists(String code, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the CurrencyService to check whether the currency {} exists. Returning FALSE as fallback: {}",
        code,
        throwable.getMessage());
    return false;
  }
}
