package it.moneyverse.transaction.model.events;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.transaction.model.entities.Transaction;

public class TransactionEventFactory {

  public static TransactionEvent createEvent(Transaction transaction, EventTypeEnum eventType) {
    return createEventBuilder(transaction).withEventType(eventType).build();
  }

  public static TransactionEvent createEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    return createEventBuilder(transaction)
        .withPreviousTransaction(createEventBuilder(originalTransaction).build())
        .withEventType(eventType)
        .build();
  }

  private static TransactionEvent.Builder createEventBuilder(Transaction transaction) {
    return TransactionEvent.builder()
        .withTransactionId(transaction.getTransactionId())
        .withUserId(transaction.getUserId())
        .withAccountId(transaction.getAccountId())
        .withCategoryId(transaction.getCategoryId())
        .withBudgetId(transaction.getBudgetId())
        .withDate(transaction.getDate())
        .withAmount(transaction.getAmount())
        .withNormalizedAmount(transaction.getNormalizedAmount())
        .withCurrency(transaction.getCurrency());
  }

  private TransactionEventFactory() {}
}
