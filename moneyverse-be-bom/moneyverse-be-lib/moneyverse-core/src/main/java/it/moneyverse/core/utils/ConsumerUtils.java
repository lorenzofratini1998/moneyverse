package it.moneyverse.core.utils;

import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUtils.class);

  public static void logMessage(ConsumerRecord<UUID, String> record, String topic) {
    LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
  }

  private ConsumerUtils() {}
}
