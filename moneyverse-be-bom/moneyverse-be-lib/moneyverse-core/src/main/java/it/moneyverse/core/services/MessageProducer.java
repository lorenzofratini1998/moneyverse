package it.moneyverse.core.services;

import it.moneyverse.core.model.events.MessageEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

public class MessageProducer<K, V> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

  private final KafkaTemplate<K, V> kafkaTemplate;

  public MessageProducer(KafkaTemplate<K, V> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public CompletableFuture<SendResult<K, V>> send(MessageEvent<K, V> event, String topic) {
    K key = event.key();
    V value = event.value();
    ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, value);

    return kafkaTemplate
        .send(producerRecord)
        .toCompletableFuture()
        .thenApply(
            sendResult -> {
              LOGGER.info(
                  "Sent event successfully. Key: {}, Topic: {}, Partition: {}, Value: {}",
                  sendResult.getProducerRecord().key(),
                  sendResult.getProducerRecord().topic(),
                  sendResult.getProducerRecord().partition(),
                  sendResult.getProducerRecord().value());
              return sendResult;
            })
        .exceptionally(
            throwable -> {
              LOGGER.error(
                  "Failed to send event. Key: {}, Error: {}",
                  key,
                  throwable.getMessage(),
                  throwable);
              throw new CompletionException("Error sending event", throwable);
            });
  }
}
