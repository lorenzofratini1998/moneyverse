package it.moneyverse.currency.services;

import static it.moneyverse.currency.utils.ExchangeRateUtils.URL;

import it.moneyverse.core.exceptions.HttpRequestException;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.currency.utils.ExchangeRateUtils;
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
    return response;
  }

  private String getCurrenciesURL() {
    return currencyRepository.findAll().stream()
        .map(Currency::getCode)
        .map(String::toUpperCase)
        .collect(Collectors.joining("+"));
  }

  @Async
  @Override
  public void initializeExchangeRates() {
    LocalDate startPeriod = exchangeRateRepository.findMinDate().orElse(ExchangeRateUtils.MIN_DATE);
    LocalDate endPeriod = LocalDate.now();
    ResponseEntity<String> response = readExchangeRates(startPeriod, endPeriod);
    List<ExchangeRate> exchangeRates =
        ExchangeRateUtils.parseXML(Objects.requireNonNull(response.getBody()));
    exchangeRateRepository.saveAll(exchangeRates);
  }
}
