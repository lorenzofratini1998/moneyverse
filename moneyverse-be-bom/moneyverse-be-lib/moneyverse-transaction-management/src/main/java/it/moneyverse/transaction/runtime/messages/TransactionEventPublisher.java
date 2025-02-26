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
    TransactionEvent event = createTransactionEvent(transaction, eventType).build();
    eventPublisher.publishEvent(event);
  }

  public void publishEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    TransactionEvent.Builder event = createTransactionEvent(transaction, eventType);
    event.withPreviousTransaction(createTransactionEvent(originalTransaction).build());
    eventPublisher.publishEvent(event.build());
  }

  private TransactionEvent.Builder createTransactionEvent(
      Transaction transaction, EventTypeEnum eventType) {
    return createTransactionEvent(transaction).withEventType(eventType);
  }

  private TransactionEvent.Builder createTransactionEvent(Transaction transaction) {
    return TransactionEvent.builder()
        .withTransactionId(transaction.getTransactionId())
        .withAccountId(transaction.getAccountId())
        .withCategoryId(transaction.getCategoryId())
        .withBudgetId(transaction.getBudgetId())
        .withDate(transaction.getDate())
        .withAmount(transaction.getAmount())
        .withNormalizedAmount(transaction.getNormalizedAmount())
        .withCurrency(transaction.getCurrency());
  }
}
