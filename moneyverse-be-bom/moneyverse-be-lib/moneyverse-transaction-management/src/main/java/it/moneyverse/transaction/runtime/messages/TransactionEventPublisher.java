package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public TransactionEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishEvent(Transfer transfer, EventTypeEnum eventType) {
    publishEvent(transfer.getTransactionFrom(), eventType);
    publishEvent(transfer.getTransactionTo(), eventType);
  }

  public void publishEvent(Subscription subscription, EventTypeEnum eventType) {
    for (Transaction transaction : subscription.getTransactions()) {
      publishEvent(transaction, eventType);
    }
  }

  public void publishEvent(Transaction transaction, EventTypeEnum eventType) {
    TransactionEvent event = new TransactionEvent();
    event.setTransactionId(transaction.getTransactionId());
    event.setAccountId(transaction.getAccountId());
    event.setCategoryId(transaction.getCategoryId());
    event.setDate(transaction.getDate());
    event.setAmount(transaction.getAmount());
    event.setNormalizedAmount(transaction.getNormalizedAmount());
    event.setCurrency(transaction.getCurrency());
    event.setEventType(eventType);
    eventPublisher.publishEvent(event);
  }
}
