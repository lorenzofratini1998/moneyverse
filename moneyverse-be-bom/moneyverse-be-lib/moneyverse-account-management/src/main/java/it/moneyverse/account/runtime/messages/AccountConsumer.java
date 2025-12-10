package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.services.AccountService;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.model.repositories.ProcessedEventRepository;
import it.moneyverse.core.runtime.messages.AbstractConsumer;
import it.moneyverse.core.utils.JsonUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class AccountConsumer extends AbstractConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountConsumer.class);
  private final AccountService accountService;

  public AccountConsumer(
      AccountService accountService, ProcessedEventRepository processedEventRepository) {
    super(processedEventRepository);
    this.accountService = accountService;
  }

  @RetryableTopic(
      backoff = @Backoff(delay = 1500, multiplier = 1.5, maxDelay = 15000),
      autoCreateTopics = "false",
      include = {RecoverableDataAccessException.class},
      concurrency = "1")
  @KafkaListener(
      topics = UserDeletionTopic.TOPIC,
      autoStartup = "true",
      concurrency = "1",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onUserDeletion(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
    UserEvent event = JsonUtils.fromJson(record.value(), UserEvent.class);
    accountService.deleteAccountsByUserId(event.key());
  }

  @RetryableTopic(
      backoff = @Backoff(delay = 1500, multiplier = 1.5, maxDelay = 15000),
      autoCreateTopics = "false",
      include = {RecoverableDataAccessException.class},
      concurrency = "1")
  @KafkaListener(
      topics = TransactionCreationTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionCreation(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key())) {
      accountService.incrementAccountBalance(
          event.getAccountId(),
          event.getAmount(),
          event.getCurrency(),
          event.getDate(),
          event.getUserId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

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
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key())) {
      accountService.decrementAccountBalance(
          event.getAccountId(),
          event.getAmount(),
          event.getCurrency(),
          event.getDate(),
          event.getUserId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
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
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key())) {
      LOGGER.info(
          "Undoing transaction {} on account {}",
          event.getPreviousTransaction().getTransactionId(),
          event.getPreviousTransaction().getAccountId());
      applyTransaction(
          event.getPreviousTransaction().getAccountId(),
          event.getPreviousTransaction().getAmount().negate(),
          event.getPreviousTransaction().getCurrency(),
          event.getPreviousTransaction().getDate(),
          event.getPreviousTransaction().getUserId());
      LOGGER.info(
          "Applying transaction {} on account {}", event.getTransactionId(), event.getAccountId());
      applyTransaction(
          event.getAccountId(),
          event.getAmount(),
          event.getCurrency(),
          event.getDate(),
          event.getUserId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  private void applyTransaction(
      UUID accountId, BigDecimal amount, String currency, LocalDate date, UUID userId) {
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      accountService.incrementAccountBalance(accountId, amount, currency, date, userId);
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      accountService.decrementAccountBalance(accountId, amount.abs(), currency, date, userId);
    }
  }
}
