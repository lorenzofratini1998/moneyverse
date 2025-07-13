package it.moneyverse.account.runtime.messages;

import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Deprecated
@Component
public class AccountEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountEventListener.class);
  private final MessageProducer<UUID, String> messageProducer;
  private final AccountTopicResolver accountTopicResolver;

  public AccountEventListener(
      MessageProducer<UUID, String> messageProducer, AccountTopicResolver accountTopicResolver) {
    this.messageProducer = messageProducer;
    this.accountTopicResolver = accountTopicResolver;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleAccountEvent(AccountEvent event) {
    LOGGER.info("Sending account event: {}", event);
    messageProducer.send(event, accountTopicResolver.resolveTopic(event));
  }
}
