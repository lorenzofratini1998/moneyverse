package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class TransactionDeletionTopic extends NewTopic {
  public static final String TOPIC = "transaction-deletion-topic";

  public TransactionDeletionTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
