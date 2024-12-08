package it.moneyverse.test.extensions.testcontainers;

import org.testcontainers.kafka.ConfluentKafkaContainer;

public class KafkaContainer extends ConfluentKafkaContainer {

  private static final String KAFKA_IMAGE = "confluentinc/cp-kafka";
  private static final String KAFKA_VERSION = "7.8.0";

  public KafkaContainer() {
    this(KAFKA_IMAGE + ":" + KAFKA_VERSION);
  }

  public KafkaContainer(String image) {
    super(image);
  }
}
