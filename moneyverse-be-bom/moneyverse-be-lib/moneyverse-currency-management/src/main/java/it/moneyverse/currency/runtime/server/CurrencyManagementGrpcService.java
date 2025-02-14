package it.moneyverse.currency.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.grpc.lib.CurrencyRequest;
import it.moneyverse.grpc.lib.CurrencyResponse;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CurrencyManagementGrpcService extends CurrencyServiceGrpc.CurrencyServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyManagementGrpcService.class);
  private final CurrencyRepository currencyRepository;

  public CurrencyManagementGrpcService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
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
}
