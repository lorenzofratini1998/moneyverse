package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.model.event.AccountDeletionEvent;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountEventListener.class);
  private final MessageProducer<UUID, String> messageProducer;

  public AccountEventListener(MessageProducer<UUID, String> messageProducer) {
    this.messageProducer = messageProducer;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleAccountDeletion(AccountDeletionEvent event) {
    LOGGER.info("Sending account deletion event: {}", event);
    messageProducer.send(event, AccountDeletionTopic.TOPIC);
  }
}
