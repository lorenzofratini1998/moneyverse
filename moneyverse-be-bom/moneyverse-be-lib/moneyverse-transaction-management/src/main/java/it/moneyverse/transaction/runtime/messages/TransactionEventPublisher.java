package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import it.moneyverse.core.runtime.messages.AbstractEventPublisher;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.factories.TransactionEventFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher extends AbstractEventPublisher<TransactionEvent> {

  private final ApplicationEventPublisher eventPublisher;
  private final TransactionTopicResolver transactionTopicResolver;
  private final OutboxEventRepository outboxEventRepository;

  public TransactionEventPublisher(
      ApplicationEventPublisher eventPublisher,
      TransactionTopicResolver transactionTopicResolver,
      OutboxEventRepository outboxEventRepository) {
    this.eventPublisher = eventPublisher;
    this.transactionTopicResolver = transactionTopicResolver;
    this.outboxEventRepository = outboxEventRepository;
  }

  @Deprecated
  public void publishEvent(Transfer transfer, EventTypeEnum eventType) {
    publishEvent(transfer.getTransactionFrom(), eventType);
    publishEvent(transfer.getTransactionTo(), eventType);
  }

  @Deprecated
  public void publishEvent(Transfer transfer, Transfer originalTransfer, EventTypeEnum eventType) {
    publishEvent(transfer.getTransactionFrom(), originalTransfer.getTransactionFrom(), eventType);
    publishEvent(transfer.getTransactionTo(), originalTransfer.getTransactionTo(), eventType);
  }

  @Deprecated
  public void publishEvent(Subscription subscription, EventTypeEnum eventType) {
    subscription.getTransactions().forEach(transaction -> publishEvent(transaction, eventType));
  }

  @Deprecated
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

  @Deprecated
  public void publishEvent(List<Transaction> transactions, EventTypeEnum eventType) {
    for (Transaction transaction : transactions) {
      publishEvent(transaction, eventType);
    }
  }

  @Deprecated
  public void publishEvent(Transaction transaction, EventTypeEnum eventType) {
    eventPublisher.publishEvent(TransactionEventFactory.createEvent(transaction, eventType));
  }

  @Deprecated
  public void publishEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    eventPublisher.publishEvent(
        TransactionEventFactory.createEvent(transaction, originalTransaction, eventType));
  }

  public void publish(Transfer transfer, Transfer originalTransfer, EventTypeEnum eventType) {
    publish(transfer.getTransactionFrom(), originalTransfer.getTransactionFrom(), eventType);
    publish(transfer.getTransactionTo(), originalTransfer.getTransactionTo(), eventType);
  }

  public void publish(Transfer transfer, EventTypeEnum eventType) {
    publish(transfer.getTransactionFrom(), eventType);
    publish(transfer.getTransactionTo(), eventType);
  }

  public void publish(Subscription subscription, EventTypeEnum eventType) {
    subscription.getTransactions().forEach(transaction -> publish(transaction, eventType));
  }

  public void publish(
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

  public void publish(List<Transaction> transactions, EventTypeEnum eventType) {
    List<OutboxEvent> events = transactions.stream().map(tx -> createEvent(tx, eventType)).toList();
    outboxEventRepository.saveAll(events);
  }

  public void publish(Transaction transaction, EventTypeEnum eventType) {
    OutboxEvent event = createEvent(transaction, eventType);
    outboxEventRepository.save(event);
  }

  public void publish(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    OutboxEvent event = createEvent(transaction, originalTransaction, eventType);
    outboxEventRepository.save(event);
  }

  private OutboxEvent createEvent(Transaction transaction, EventTypeEnum eventType) {
    TransactionEvent txEvent = TransactionEventFactory.createEvent(transaction, eventType);
    return buildOutboxEvent(
        transaction.getTransactionId(),
        transactionTopicResolver.resolveTopic(txEvent),
        AggregateTypeEnum.TRANSACTION,
        eventType,
        txEvent);
  }

  private OutboxEvent createEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    TransactionEvent txEvent =
        TransactionEventFactory.createEvent(transaction, originalTransaction, eventType);
    return buildOutboxEvent(
        transaction.getTransactionId(),
        transactionTopicResolver.resolveTopic(txEvent),
        AggregateTypeEnum.TRANSACTION,
        eventType,
        txEvent);
  }
}
