package it.moneyverse.currency.runtime.controllers;

import it.moneyverse.core.model.dto.CurrencyDto;
import java.util.List;
import java.util.Optional;

public interface CurrencyOperations {

  List<CurrencyDto> getCurrencies(Optional<Boolean> enabled);
}
