package it.moneyverse.currency.runtime.batch;

import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateWriter implements ItemWriter<List<ExchangeRate>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateWriter.class);
  private final ExchangeRateRepository exchangeRateRepository;

  public ExchangeRateWriter(ExchangeRateRepository exchangeRateRepository) {
    this.exchangeRateRepository = exchangeRateRepository;
  }

  @Override
  public void write(Chunk<? extends List<ExchangeRate>> chunk) {
    LOGGER.info("Writing exchange-rates to batch");
    List<ExchangeRate> entities = chunk.getItems().stream().flatMap(List::stream).toList();
    if (!entities.isEmpty()) {
      exchangeRateRepository.saveAll(entities);
    }
  }
}
