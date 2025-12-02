package it.moneyverse.analytics.runtime.batch;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.analytics.model.mapper.TransactionEventMapper;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProcessor
    implements ItemProcessor<List<TransactionEventBuffer>, List<TransactionEvent>> {

  @Override
  public List<TransactionEvent> process(List<TransactionEventBuffer> item) {
    return TransactionEventMapper.toTransactionEvents(item);
  }
}
