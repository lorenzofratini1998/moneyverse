package it.moneyverse.core.runtime.messages;

import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class OutboxProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxProcessor.class);
  private final OutboxPoller outboxPoller;
  private final OutboxPublisher outboxPublisher;
  private final OutboxEventRepository outboxEventRepository;

  public OutboxProcessor(
      OutboxPoller outboxPoller,
      OutboxPublisher outboxPublisher,
      OutboxEventRepository outboxEventRepository) {
    this.outboxPoller = outboxPoller;
    this.outboxPublisher = outboxPublisher;
    this.outboxEventRepository = outboxEventRepository;
  }

  @Scheduled(fixedRate = 5000)
  public void process() {
    List<OutboxEvent> events = outboxPoller.pollUnprocessedEvents();
    for (OutboxEvent event : events) {
      try {
        outboxPublisher.publish(event);
        event.setProcessed(true);
        outboxEventRepository.save(event);
      } catch (Exception e) {
        LOGGER.error(
            "Error publishing event: {} to topic {}", event.getPayload(), event.getTopic(), e);
      }
    }
  }
}
