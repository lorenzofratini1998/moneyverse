package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class UserCreationTopic extends NewTopic {

  public static final String TOPIC = "user-creation-topic";

  public UserCreationTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
