package it.moneyverse.analytics.runtime.messages;

import it.moneyverse.analytics.model.mapper.TransactionEventBufferMapper;
import it.moneyverse.analytics.model.repositories.TransactionEventBufferRepository;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.repositories.ProcessedEventRepository;
import it.moneyverse.core.runtime.messages.AbstractConsumer;
import it.moneyverse.core.utils.JsonUtils;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DashboardConsumer extends AbstractConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardConsumer.class);

  private final TransactionEventBufferRepository transactionEventBufferRepository;

  public DashboardConsumer(
      ProcessedEventRepository processedEventRepository,
      TransactionEventBufferRepository transactionEventBufferRepository) {
    super(processedEventRepository);
    this.transactionEventBufferRepository = transactionEventBufferRepository;
  }

  @Transactional
  @RetryableTopic(
      backoff = @Backoff(delay = 1500, multiplier = 1.5, maxDelay = 15000),
      autoCreateTopics = "false",
      include = {RecoverableDataAccessException.class},
      concurrency = "1")
  @KafkaListener(
      topics = TransactionCreationTopic.TOPIC,
      autoStartup = "true",
      concurrency = "1",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionCreation(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    processEvent(record, topic);
  }

  @Transactional
  @RetryableTopic(
      backoff = @Backoff(delay = 1500, multiplier = 1.5, maxDelay = 15000),
      autoCreateTopics = "false",
      include = {RecoverableDataAccessException.class},
      concurrency = "1")
  @KafkaListener(
      topics = TransactionDeletionTopic.TOPIC,
      autoStartup = "true",
      concurrency = "1",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionDeletion(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    processEvent(record, topic);
  }

  @Transactional
  @RetryableTopic(
      backoff = @Backoff(delay = 1500, multiplier = 1.5, maxDelay = 15000),
      autoCreateTopics = "false",
      include = {RecoverableDataAccessException.class},
      concurrency = "1")
  @KafkaListener(
      topics = TransactionUpdateTopic.TOPIC,
      autoStartup = "true",
      concurrency = "1",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionUpdate(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    processEvent(record, topic);
  }

  private void processEvent(ConsumerRecord<UUID, String> record, String topic) {
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key())) {
      transactionEventBufferRepository.save(
          TransactionEventBufferMapper.toTransactionEventBuffer(event));
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }
}
