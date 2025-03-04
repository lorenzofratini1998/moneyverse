package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.events.TransactionEventFactory;
import java.util.List;
import java.util.UUID;
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

  public void publishEvent(Transfer transfer, Transfer originalTransfer, EventTypeEnum eventType) {
    publishEvent(transfer.getTransactionFrom(), originalTransfer.getTransactionFrom(), eventType);
    publishEvent(transfer.getTransactionTo(), originalTransfer.getTransactionTo(), eventType);
  }

  public void publishEvent(Subscription subscription, EventTypeEnum eventType) {
    subscription.getTransactions().forEach(transaction -> publishEvent(transaction, eventType));
  }

  public void publishEvent(
      Subscription subscription, Subscription originalSubscription, EventTypeEnum eventType) {
    subscription
        .getTransactions()
        .forEach(
            transaction -> {
              Transaction originalTransaction =
                  getTransaction(originalSubscription, transaction.getTransactionId());
              publishEvent(transaction, originalTransaction, eventType);
            });
  }

  private Transaction getTransaction(Subscription subscription, UUID transactionId) {
    return subscription.getTransactions().stream()
        .filter(original -> original.getTransactionId().equals(transactionId))
        .findFirst()
        .orElse(null);
  }

  public void publishEvent(List<Transaction> transactions, EventTypeEnum eventType) {
    for (Transaction transaction : transactions) {
      publishEvent(transaction, eventType);
    }
  }

  public void publishEvent(Transaction transaction, EventTypeEnum eventType) {
    eventPublisher.publishEvent(TransactionEventFactory.createEvent(transaction, eventType));
  }

  public void publishEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    eventPublisher.publishEvent(
        TransactionEventFactory.createEvent(transaction, originalTransaction, eventType));
  }
}
