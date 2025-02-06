package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class TransactionCreationTopic extends NewTopic {
  public static final String TOPIC = "transaction-creation-topic";

  public TransactionCreationTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
