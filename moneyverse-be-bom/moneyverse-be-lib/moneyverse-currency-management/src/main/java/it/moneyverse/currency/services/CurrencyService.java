package it.moneyverse.currency.services;

import it.moneyverse.currency.model.dto.CurrencyDto;
import java.util.List;

public interface CurrencyService {

  List<CurrencyDto> getCurrencies();
}
