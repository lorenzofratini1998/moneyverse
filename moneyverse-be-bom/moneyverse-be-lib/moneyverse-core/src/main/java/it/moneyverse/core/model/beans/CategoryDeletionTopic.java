package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class CategoryDeletionTopic extends NewTopic {

  public static final String TOPIC = "category-deletion-topic";

  public CategoryDeletionTopic() {
    super(TOPIC, 1, (short) 1);
  }
}
