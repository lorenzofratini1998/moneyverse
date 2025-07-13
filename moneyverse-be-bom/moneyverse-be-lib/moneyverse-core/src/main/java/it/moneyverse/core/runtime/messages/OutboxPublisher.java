package it.moneyverse.core.runtime.messages;

import it.moneyverse.core.model.entities.OutboxEvent;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class OutboxPublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxPublisher.class);
  private final KafkaTemplate<UUID, String> kafkaTemplate;

  public OutboxPublisher(KafkaTemplate<UUID, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(OutboxEvent event) {
    LOGGER.info("Sending event: {} to topic {}", event.getPayload(), event.getTopic());
    kafkaTemplate.send(event.getTopic(), event.getEventId(), event.getPayload());
  }
}
