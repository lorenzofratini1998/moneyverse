package it.moneyverse.transaction.model.factories;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class TransactionEventFactory {

  public static TransactionEvent createEvent(Transaction transaction, EventTypeEnum eventType) {
    return createEventBuilder(transaction)
        .withEventType(eventType)
        .withEventTimestamp(LocalDateTime.now())
        .build();
  }

  public static TransactionEvent createEvent(
      Transaction transaction, Transaction originalTransaction, EventTypeEnum eventType) {
    return createEventBuilder(transaction)
        .withPreviousTransaction(createEventBuilder(originalTransaction).build())
        .withEventType(eventType)
        .withEventTimestamp(LocalDateTime.now())
        .build();
  }

  private static TransactionEvent.Builder createEventBuilder(Transaction transaction) {
    return TransactionEvent.builder()
        .withTransactionId(transaction.getTransactionId())
        .withUserId(transaction.getUserId())
        .withAccountId(transaction.getAccountId())
        .withCategoryId(transaction.getCategoryId())
        .withBudgetId(transaction.getBudgetId())
        .withTags(transaction.getTags().stream().map(Tag::getTagId).collect(Collectors.toSet()))
        .withDate(transaction.getDate())
        .withAmount(transaction.getAmount())
        .withNormalizedAmount(transaction.getNormalizedAmount())
        .withCurrency(transaction.getCurrency());
  }

  private TransactionEventFactory() {}
}
