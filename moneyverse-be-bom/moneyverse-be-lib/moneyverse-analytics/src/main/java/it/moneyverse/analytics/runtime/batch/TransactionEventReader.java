package it.moneyverse.analytics.runtime.batch;

import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.analytics.model.repositories.TransactionEventBufferRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventReader implements ItemReader<List<TransactionEventBuffer>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventReader.class);
  private static final int BATCH_SIZE = 500;

  private final TransactionEventBufferRepository transactionEventBufferRepository;

  public TransactionEventReader(TransactionEventBufferRepository transactionEventBufferRepository) {
    this.transactionEventBufferRepository = transactionEventBufferRepository;
  }

  @Override
  public List<TransactionEventBuffer> read() {
    List<TransactionEventBuffer> items =
        transactionEventBufferRepository.findAll(PageRequest.of(0, BATCH_SIZE)).getContent();

    if (items.isEmpty()) {
      LOGGER.info("No more items to process. Ending step.");
      return null;
    }

    LOGGER.info("Read {} transaction event buffers", items.size());
    return items;
  }
}
