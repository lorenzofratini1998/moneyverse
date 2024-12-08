package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class AccountDeletionTopic extends NewTopic {

  private static final String name = "account-deletion-topic";

  public AccountDeletionTopic() {
    super(name, 1, (short) 1);
  }
}
