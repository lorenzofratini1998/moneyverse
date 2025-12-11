package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class AccountDeletionTopic extends NewTopic {

  public static final String TOPIC = "account-deletion-topic";

  public AccountDeletionTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
