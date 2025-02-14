package it.moneyverse.core.services;

import it.moneyverse.core.model.dto.CurrencyDto;
import java.util.Optional;

public interface CurrencyServiceClient {

  Optional<CurrencyDto> getCurrencyByCode(String code);

  void checkIfCurrencyExists(String currency);
}
