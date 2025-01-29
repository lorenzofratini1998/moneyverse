package it.moneyverse.currency.services;

import it.moneyverse.currency.model.dto.CurrencyDto;
import java.util.List;
import java.util.Optional;

public interface CurrencyService {

  List<CurrencyDto> getCurrencies(Optional<Boolean> enabled);
}
