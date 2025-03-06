package it.moneyverse.currency.runtime.batch;

import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.factories.ExchangeRateFactory;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateProcessor implements ItemProcessor<String, List<ExchangeRate>> {

  @Override
  public List<ExchangeRate> process(@NonNull String item) {
    return ExchangeRateFactory.createExchangeRates(item);
  }
}
