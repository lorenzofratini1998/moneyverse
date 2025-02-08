package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class TransactionUpdateTopic extends NewTopic {
  public static final String TOPIC = "transaction-update-topic";

  public TransactionUpdateTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
