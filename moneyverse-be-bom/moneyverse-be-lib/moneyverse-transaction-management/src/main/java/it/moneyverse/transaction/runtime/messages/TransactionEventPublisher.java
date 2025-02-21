package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public TransactionEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishEvent(Subscription subscription, EventTypeEnum eventType) {
    for (Transaction transaction : subscription.getTransactions()) {
      TransactionEvent event = new TransactionEvent();
      event.setTransactionId(transaction.getTransactionId());
      event.setAccountId(subscription.getAccountId());
      event.setCategoryId(subscription.getCategoryId());
      event.setDate(transaction.getDate());
      event.setAmount(transaction.getAmount());
      event.setEventType(eventType);
      eventPublisher.publishEvent(event);
    }
  }
}
