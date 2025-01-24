package it.moneyverse.core.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ConsumerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUtils.class);

    public static void logMessage(ConsumerRecord<UUID, String> record, String topic) {
        LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
    }

    private ConsumerUtils() {}
}
