package it.moneyverse.currency.runtime.batch;

import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import java.time.LocalDate;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExchangeRateReader implements ItemReader<String> {

  private static final String URL =
      "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?startPeriod=%s&endPeriod=%s&detail=dataonly&format=structurespecificdata";

  private final RestTemplate restTemplate;
  private final CurrencyRepository currencyRepository;
  private boolean batchJobState = false;

  public ExchangeRateReader(RestTemplate restTemplate, CurrencyRepository currencyRepository) {
    this.restTemplate = restTemplate;
    this.currencyRepository = currencyRepository;
  }

  @Override
  public String read() {
    if (!batchJobState) {
      final String currencies =
          currencyRepository.findAll().stream()
              .map(Currency::getCode)
              .map(String::toUpperCase)
              .collect(Collectors.joining("+"));
      ResponseEntity<String> response =
          restTemplate.getForEntity(
              URL.formatted(currencies, LocalDate.now(), LocalDate.now()), String.class);
      batchJobState = true;
      return response.getBody();
    }
    return null;
  }
}
