package it.moneyverse.currency.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.utils.CurrencyMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyManagementServiceTest {

  @InjectMocks private CurrencyManagementService currencyManagementService;

  @Mock private CurrencyRepository currencyRepository;
  private MockedStatic<CurrencyMapper> mapper;

  @BeforeEach
  public void setup() {
    mapper = mockStatic(CurrencyMapper.class);
  }

  @AfterEach
  public void tearDown() {
    mapper.close();
  }

  @Test
  void WhenGetCurrencies_ThenReturnCurrencies(
      @Mock List<Currency> currencies, @Mock List<CurrencyDto> response) {
    when(currencyRepository.findAll()).thenReturn(currencies);
    mapper.when(() -> CurrencyMapper.toCurrencyDto(currencies)).thenReturn(response);

    response = currencyManagementService.getCurrencies(Optional.empty());

    assertNotNull(response);
    verify(currencyRepository, times(1)).findAll();
    mapper.verify(() -> CurrencyMapper.toCurrencyDto(currencies), times(1));
  }

  @Test
  void GivenEnabled_WhenGetCurrencies_ThenReturnCurrencies(
      @Mock List<Currency> currencies, @Mock List<CurrencyDto> response) {
    when(currencyRepository.findByIsEnabled(true)).thenReturn(currencies);
    mapper.when(() -> CurrencyMapper.toCurrencyDto(currencies)).thenReturn(response);

    response = currencyManagementService.getCurrencies(Optional.of(true));

    assertNotNull(response);
    verify(currencyRepository, times(1)).findByIsEnabled(true);
    mapper.verify(() -> CurrencyMapper.toCurrencyDto(currencies), times(1));
  }
}
