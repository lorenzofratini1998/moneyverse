package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);
  private final MessageProducer<UUID, String> messageProducer;

  public TransactionEventListener(MessageProducer<UUID, String> messageProducer) {
    this.messageProducer = messageProducer;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTransactionCreation(TransactionEvent event) {
    LOGGER.info("Publishing transaction event: {}", event);
    switch (event.getEventType()) {
      case CREATE -> messageProducer.send(event, TransactionCreationTopic.TOPIC);
      case UPDATE -> messageProducer.send(event, TransactionUpdateTopic.TOPIC);
      case DELETE -> messageProducer.send(event, TransactionDeletionTopic.TOPIC);
      default -> throw new IllegalStateException("Unexpected value: " + event.getEventType());
    }
  }
}
