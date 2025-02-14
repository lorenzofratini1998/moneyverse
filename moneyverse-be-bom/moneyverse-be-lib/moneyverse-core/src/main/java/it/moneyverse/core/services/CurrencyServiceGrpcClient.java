package it.moneyverse.core.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.CurrencyDto;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceGrpcClient implements CurrencyServiceClient {

  private final CurrencyGrpcService currencyGrpcService;

  public CurrencyServiceGrpcClient(CurrencyGrpcService currencyGrpcService) {
    this.currencyGrpcService = currencyGrpcService;
  }

  @Override
  public Optional<CurrencyDto> getCurrencyByCode(String code) {
    return currencyGrpcService.getCurrencyByCode(code);
  }

  @Override
  public void checkIfCurrencyExists(String currency) {
    if (currencyGrpcService.getCurrencyByCode(currency).isEmpty()) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }
}
