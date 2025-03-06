package it.moneyverse.currency.services;

import it.moneyverse.core.exceptions.HttpRequestException;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.factories.ExchangeRateFactory;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateManagementService implements ExchangeRateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateManagementService.class);
  private static final String URL =
      "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?startPeriod=%s&endPeriod=%s&detail=dataonly&format=structurespecificdata";
  private static final LocalDate START_DATE = LocalDate.of(2002, 1, 2);

  private final RestTemplate restTemplate;
  private final CurrencyRepository currencyRepository;
  private final ExchangeRateRepository exchangeRateRepository;

  public ExchangeRateManagementService(
      RestTemplate restTemplate,
      CurrencyRepository currencyRepository,
      ExchangeRateRepository exchangeRateRepository) {
    this.restTemplate = restTemplate;
    this.currencyRepository = currencyRepository;
    this.exchangeRateRepository = exchangeRateRepository;
  }

  @Override
  public ResponseEntity<String> readExchangeRates(LocalDate startPeriod, LocalDate endPeriod) {
    final String currencies = getCurrenciesURL();
    ResponseEntity<String> response =
        restTemplate.getForEntity(URL.formatted(currencies, startPeriod, endPeriod), String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      LOGGER.info("Failed to read exchange rates from {}, ex: {}", URL, response.getBody());
      throw new HttpRequestException(
          "Failed to read exchange rates from %s with status code: %s and body: %s"
              .formatted(URL, response.getStatusCode(), response.getBody()));
    }
    String body = response.getBody();
    if (body != null && body.isEmpty()) {
      return readExchangeRates(startPeriod.minusDays(1), endPeriod.minusDays(1));
    }
    return response;
  }

  private String getCurrenciesURL() {
    return currencyRepository.findByIsEnabled(true).stream()
        .map(Currency::getCode)
        .map(String::toUpperCase)
        .collect(Collectors.joining("+"));
  }

  @Async
  @Override
  public void initializeExchangeRates() {
    LocalDate startPeriod = exchangeRateRepository.findMaxDate().orElse(START_DATE);
    LocalDate endPeriod = LocalDate.now();
    ResponseEntity<String> response = readExchangeRates(startPeriod, endPeriod);
    if (exchangeRateRepository.existsExchangeRatesByDate(startPeriod)
        || exchangeRateRepository.existsExchangeRatesByDate(endPeriod)) {
      LOGGER.info("Exchange rates already initialized from {} to {}", startPeriod, endPeriod);
      return;
    }
    List<ExchangeRate> exchangeRates =
        ExchangeRateFactory.createExchangeRates(Objects.requireNonNull(response.getBody()));
    saveExchangeRates(exchangeRates);
    LOGGER.info("Exchange rates initialized from {} to {}", startPeriod, endPeriod);
  }

  private void saveExchangeRates(List<ExchangeRate> exchangeRates) {
    LOGGER.info("Saving {} exchange rates...", exchangeRates.size());
    int batchSize = 1000;
    for (int i = 0; i < exchangeRates.size(); i += batchSize) {
      int end = Math.min(i + batchSize, exchangeRates.size());
      List<ExchangeRate> batch = exchangeRates.subList(i, end);
      exchangeRateRepository.saveAll(batch);
      exchangeRateRepository.flush();
    }
  }
}
