package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Deprecated
@Component
public class TransactionEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);
  private final MessageProducer<UUID, String> messageProducer;
  private final TransactionTopicResolver topicResolver;

  public TransactionEventListener(
      MessageProducer<UUID, String> messageProducer, TransactionTopicResolver topicResolver) {
    this.messageProducer = messageProducer;
    this.topicResolver = topicResolver;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTransactionCreation(TransactionEvent event) {
    LOGGER.info("Sending transaction event: {}", event);
    messageProducer.send(event, topicResolver.resolveTopic(event));
  }
}
