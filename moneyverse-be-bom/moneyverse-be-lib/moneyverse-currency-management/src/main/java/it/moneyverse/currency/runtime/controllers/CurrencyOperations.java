package it.moneyverse.currency.runtime.controllers;

import it.moneyverse.currency.model.dto.CurrencyDto;
import java.util.List;

public interface CurrencyOperations {

  List<CurrencyDto> getCurrencies();
}
