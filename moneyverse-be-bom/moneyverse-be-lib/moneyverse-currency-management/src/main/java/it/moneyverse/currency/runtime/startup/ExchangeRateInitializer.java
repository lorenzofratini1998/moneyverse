package it.moneyverse.currency.runtime.startup;

import it.moneyverse.currency.services.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
    prefix = "spring.runner.initializer",
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@Component
public class ExchangeRateInitializer implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateInitializer.class);

  private final ExchangeRateService exchangeRateService;

  public ExchangeRateInitializer(ExchangeRateService exchangeRateService) {
    this.exchangeRateService = exchangeRateService;
  }

  @Override
  public void run(ApplicationArguments args) {
    LOGGER.info("Initialize the exchange rate table");
    exchangeRateService.initializeExchangeRates();
  }
}
