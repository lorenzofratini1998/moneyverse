package it.moneyverse.currency.runtime.batch;

import it.moneyverse.currency.services.ExchangeRateService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateReader implements ItemReader<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateReader.class);

  private final ExchangeRateService exchangeRateService;

  private boolean batchJobState = false;

    public ExchangeRateReader(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
  public String read() {
    if (!batchJobState) {
      LOGGER.info("Reading exchange-rates from batch for date: {}", LocalDate.now());
      ResponseEntity<String> response = exchangeRateService.readExchangeRates(LocalDate.now(), LocalDate.now());
      batchJobState = true;
      return response.getBody();
    }
    return null;
  }
}
