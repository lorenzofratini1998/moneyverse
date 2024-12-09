package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class UserDeletionTopic extends NewTopic {

  public static final String TOPIC = "user-deletion-topic";

  public UserDeletionTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
