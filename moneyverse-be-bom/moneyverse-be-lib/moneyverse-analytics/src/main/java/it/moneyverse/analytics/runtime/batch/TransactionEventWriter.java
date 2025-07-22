package it.moneyverse.analytics.runtime.batch;

import it.moneyverse.analytics.enums.TransactionEventStateEnum;
import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.repositories.TransactionEventBufferRepository;
import it.moneyverse.analytics.model.repositories.TransactionEventRepository;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionEventWriter implements ItemWriter<List<TransactionEvent>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventWriter.class);
  private final TransactionEventRepository transactionEventRepository;
  private final TransactionEventBufferRepository transactionEventBufferRepository;

  public TransactionEventWriter(
      TransactionEventRepository transactionEventRepository,
      TransactionEventBufferRepository transactionEventBufferRepository) {
    this.transactionEventRepository = transactionEventRepository;
    this.transactionEventBufferRepository = transactionEventBufferRepository;
  }

  @Override
  @Transactional
  public void write(Chunk<? extends List<TransactionEvent>> chunk) {
    List<TransactionEvent> transactionEvents =
        chunk.getItems().stream().flatMap(List::stream).toList();
    if (!transactionEvents.isEmpty()) {
      List<UUID> eventIds = transactionEvents.stream().map(TransactionEvent::getEventId).toList();
      try {
        transactionEventRepository.saveAll(transactionEvents);
        transactionEventBufferRepository.deleteByEventIds(eventIds);
        LOGGER.info("Successfully processed {} transaction events", transactionEvents.size());
      } catch (Exception e) {
        LOGGER.error("Error saving transaction events", e);
        transactionEventBufferRepository.updateStateByEventIds(
            eventIds, TransactionEventStateEnum.ERROR);
      }
    }
  }
}
