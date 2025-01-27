package it.moneyverse.currency.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.moneyverse.core.exceptions.HttpRequestException;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExchangeRateManagementServiceTest {

  @InjectMocks private ExchangeRateManagementService exchangeRateManagementService;

  @Mock private CurrencyRepository currencyRepository;
  @Mock private ExchangeRateRepository exchangeRateRepository;
  @Mock private RestTemplate restTemplate;

  @Test
  void testReadExchangeRates(@Mock Currency currency) {
    when(currencyRepository.findAll()).thenReturn(List.of(currency));
    when(currency.getCode()).thenReturn(RandomUtils.randomString(3));
    String responseBody = "<mocked XML response>";
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

    ResponseEntity<String> result =
        exchangeRateManagementService.readExchangeRates(LocalDate.now(), LocalDate.now());

    assertNotNull(result.getBody());
    assertEquals(responseBody, result.getBody());
  }

  @Test
  void testReadExchangeRates_Failed(@Mock Currency currency) {
    when(currencyRepository.findAll()).thenReturn(List.of(currency));
    when(currency.getCode()).thenReturn(RandomUtils.randomString(3));
    String responseBody = "<mocked XML response>";
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND));

    assertThrows(
        HttpRequestException.class,
        () -> exchangeRateManagementService.readExchangeRates(LocalDate.now(), LocalDate.now()));
  }
}
