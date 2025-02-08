package it.moneyverse.budget.runtime.messages;

import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.events.BudgetDeletionEvent;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BudgetEventListener {

  private final MessageProducer<UUID, String> messageProducer;

  public BudgetEventListener(MessageProducer<UUID, String> messageProducer) {
    this.messageProducer = messageProducer;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBudgetDeletion(BudgetDeletionEvent event) {
    messageProducer.send(event, BudgetDeletionTopic.TOPIC);
  }
}
