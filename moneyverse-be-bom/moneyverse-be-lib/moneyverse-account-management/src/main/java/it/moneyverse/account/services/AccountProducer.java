package it.moneyverse.account.services;

import it.moneyverse.account.model.Event.AccountDeletionEvent;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class AccountProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountProducer.class);

  private final KafkaTemplate<UUID, String> kafkaTemplate;

  public AccountProducer(KafkaTemplate<UUID, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public CompletableFuture<SendResult<UUID, String>> send(
      AccountDeletionEvent event, String topic) {
    UUID key = event.accountId();
    String value = event.toString();
    ProducerRecord<UUID, String> producerRecord = new ProducerRecord<>(topic, key, value);

    return kafkaTemplate
        .send(producerRecord)
        .toCompletableFuture()
        .thenApply(
            sendResult -> {
              LOGGER.info(
                  "Sent account event successfully. Key: {}, Topic: {}, Partition: {}, Value: {}",
                  sendResult.getProducerRecord().key(),
                  sendResult.getProducerRecord().topic(),
                  sendResult.getProducerRecord().partition(),
                  sendResult.getProducerRecord().value());
              return sendResult;
            })
        .exceptionally(
            throwable -> {
              LOGGER.error(
                  "Failed to send account event. Key: {}, Error: {}",
                  key,
                  throwable.getMessage(),
                  throwable);
              throw new CompletionException("Error sending account event", throwable);
            });
  }
}
