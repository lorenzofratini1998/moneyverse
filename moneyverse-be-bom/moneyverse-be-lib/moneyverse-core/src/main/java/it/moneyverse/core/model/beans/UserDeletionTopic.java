package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class UserDeletionTopic extends NewTopic {

  private static final String name = "user-deletion-topic";

  public UserDeletionTopic() {
    super(name, 1, (short) 1);
  }
}
