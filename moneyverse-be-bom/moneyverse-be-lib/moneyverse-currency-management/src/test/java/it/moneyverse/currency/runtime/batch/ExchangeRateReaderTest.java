package it.moneyverse.currency.runtime.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.test.utils.RandomUtils;
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
public class ExchangeRateReaderTest {

  @Mock private CurrencyRepository currencyRepository;
  @Mock private RestTemplate restTemplate;
  @InjectMocks private ExchangeRateReader exchangeRateReader;

  @Test
  void testRead(@Mock Currency currency) {
    when(currencyRepository.findAll()).thenReturn(List.of(currency));
    when(currency.getCode()).thenReturn(RandomUtils.randomString(3));
    String responseBody = "<mocked XML response>";
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

    String result = exchangeRateReader.read();

    assertNotNull(result);
    assertEquals(responseBody, result);
  }
}
