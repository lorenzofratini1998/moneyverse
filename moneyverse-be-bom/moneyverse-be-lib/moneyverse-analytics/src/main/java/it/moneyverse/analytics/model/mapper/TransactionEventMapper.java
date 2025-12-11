package it.moneyverse.analytics.model.mapper;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import java.util.List;

public class TransactionEventMapper {

  public static List<TransactionEvent> toTransactionEvents(
      List<TransactionEventBuffer> transactionEventBuffers) {
    return transactionEventBuffers.stream()
        .map(TransactionEventMapper::toTransactionEvent)
        .toList();
  }

  public static TransactionEvent toTransactionEvent(TransactionEventBuffer transactionEventBuffer) {
    TransactionEvent transactionEvent = new TransactionEvent();
    transactionEvent.setEventId(transactionEventBuffer.getEventId());
    transactionEvent.setEventType(transactionEventBuffer.getEventType().toInteger());
    transactionEvent.setUserId(transactionEventBuffer.getUserId());
    transactionEvent.setTransactionId(transactionEventBuffer.getTransactionId());
    transactionEvent.setOriginalTransactionId(transactionEventBuffer.getOriginalTransactionId());
    transactionEvent.setAccountId(transactionEventBuffer.getAccountId());
    transactionEvent.setCategoryId(transactionEventBuffer.getCategoryId());
    transactionEvent.setBudgetId(transactionEventBuffer.getBudgetId());
    transactionEvent.setAmount(transactionEventBuffer.getAmount());
    transactionEvent.setNormalizedAmount(transactionEventBuffer.getNormalizedAmount());
    transactionEvent.setCurrency(transactionEventBuffer.getCurrency());
    transactionEvent.setDate(transactionEventBuffer.getDate());
    transactionEvent.setEventTimestamp(transactionEventBuffer.getEventTimestamp());
    return transactionEvent;
  }

  private TransactionEventMapper() {}
}
