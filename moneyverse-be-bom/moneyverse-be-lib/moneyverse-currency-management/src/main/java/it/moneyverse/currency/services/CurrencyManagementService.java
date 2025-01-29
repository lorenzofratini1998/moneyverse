package it.moneyverse.currency.services;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.utils.CurrencyMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrencyManagementService implements CurrencyService {

  private final CurrencyRepository currencyRepository;

  public CurrencyManagementService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurrencyDto> getCurrencies(Optional<Boolean> enabled) {
    return enabled.isPresent()
        ? CurrencyMapper.toCurrencyDto(currencyRepository.findByIsEnabled(enabled.get()))
        : CurrencyMapper.toCurrencyDto(currencyRepository.findAll());
  }
}
