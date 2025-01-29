package it.moneyverse.currency.runtime.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.services.ExchangeRateService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ExchangeRateReaderTest {

  @Mock ExchangeRateService exchangeRateService;
  @InjectMocks private ExchangeRateReader exchangeRateReader;

  @Test
  void testRead(@Mock Currency currency) {
    String responseBody = "<mocked XML response>";
    when(exchangeRateService.readExchangeRates(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

    String result = exchangeRateReader.read();

    assertNotNull(result);
    assertEquals(responseBody, result);
  }
}
