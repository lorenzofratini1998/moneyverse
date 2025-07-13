package it.moneyverse.core.runtime.messages;

import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import java.util.List;

public class OutboxPoller {

  private final OutboxEventRepository outboxEventRepository;

  public OutboxPoller(OutboxEventRepository outboxEventRepository) {
    this.outboxEventRepository = outboxEventRepository;
  }

  public List<OutboxEvent> pollUnprocessedEvents() {
    return outboxEventRepository.findAllByProcessedFalse();
  }
}
