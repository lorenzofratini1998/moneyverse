package it.moneyverse.analytics.model.mapper;

import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.core.model.events.TransactionEvent;

public class TransactionEventBufferMapper {

  public static TransactionEventBuffer toTransactionEventBuffer(TransactionEvent transactionEvent) {
    TransactionEventBuffer transactionEventBuffer = new TransactionEventBuffer();
    transactionEventBuffer.setEventType(transactionEvent.getEventType());
    transactionEventBuffer.setUserId(transactionEvent.getUserId());
    transactionEventBuffer.setTransactionId(transactionEvent.getTransactionId());
    transactionEventBuffer.setOriginalTransactionId(
        transactionEvent.getPreviousTransaction() != null
            ? transactionEvent.getPreviousTransaction().getTransactionId()
            : null);
    transactionEventBuffer.setAccountId(transactionEvent.getAccountId());
    transactionEventBuffer.setCategoryId(transactionEvent.getCategoryId());
    transactionEventBuffer.setBudgetId(transactionEvent.getBudgetId());
    transactionEventBuffer.setAmount(transactionEvent.getAmount());
    transactionEventBuffer.setNormalizedAmount(transactionEvent.getNormalizedAmount());
    transactionEventBuffer.setCurrency(transactionEvent.getCurrency());
    transactionEventBuffer.setDate(transactionEvent.getDate());
    transactionEventBuffer.setEventTimestamp(transactionEvent.getEventTimestamp());
    return transactionEventBuffer;
  }

  private TransactionEventBufferMapper() {}
}
