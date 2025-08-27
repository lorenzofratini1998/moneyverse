package it.moneyverse.core.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.ProcessedEvent;
import it.moneyverse.core.model.repositories.ProcessedEventRepository;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsumer.class);
  protected final ProcessedEventRepository processedEventRepository;

  protected AbstractConsumer(ProcessedEventRepository processedEventRepository) {
    this.processedEventRepository = processedEventRepository;
  }

  protected boolean eventNotProcessed(UUID eventId) {
    if (processedEventRepository.existsById(eventId)) {
      LOGGER.warn("Event {} already processed", eventId);
      return false;
    }
    return true;
  }

  protected void persistProcessedEvent(
      UUID eventId, String topic, EventTypeEnum eventType, String payload) {
    ProcessedEvent processedEvent = new ProcessedEvent();
    processedEvent.setEventId(eventId);
    processedEvent.setTopic(topic);
    processedEvent.setEventType(eventType);
    processedEvent.setPayload(payload);
    processedEventRepository.save(processedEvent);
  }

  protected static void logMessage(ConsumerRecord<UUID, String> record, String topic) {
    LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
  }
}
