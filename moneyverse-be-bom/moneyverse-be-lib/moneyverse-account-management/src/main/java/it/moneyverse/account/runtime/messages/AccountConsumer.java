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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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

  @RetryableTopic
  @KafkaListener(
      topics = UserDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onUserDeletion(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
    UserEvent event = JsonUtils.fromJson(record.value(), UserEvent.class);
    accountService.deleteAccountsByUserId(event.key());
  }

  @RetryableTopic
  @KafkaListener(
      topics = TransactionCreationTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionCreation(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (!eventAlreadyProcessed(record.key())) {
      accountService.incrementAccountBalance(
          event.getAccountId(), event.getAmount(), event.getCurrency(), event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @RetryableTopic
  @KafkaListener(
      topics = TransactionDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionDeletion(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (!eventAlreadyProcessed(record.key())) {
      accountService.decrementAccountBalance(
          event.getAccountId(), event.getAmount(), event.getCurrency(), event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @Transactional
  @RetryableTopic
  @KafkaListener(
      topics = TransactionUpdateTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionUpdate(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (!eventAlreadyProcessed(record.key())) {
      LOGGER.info(
          "Undoing transaction {} on account {}",
          event.getPreviousTransaction().getTransactionId(),
          event.getPreviousTransaction().getAccountId());
      applyTransaction(
          event.getPreviousTransaction().getAccountId(),
          event.getPreviousTransaction().getAmount().negate(),
          event.getPreviousTransaction().getCurrency(),
          event.getPreviousTransaction().getDate());
      LOGGER.info(
          "Applying transaction {} on account {}", event.getTransactionId(), event.getAccountId());
      applyTransaction(
          event.getAccountId(), event.getAmount(), event.getCurrency(), event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  private void applyTransaction(
      UUID accountId, BigDecimal amount, String currency, LocalDate date) {
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      accountService.incrementAccountBalance(accountId, amount, currency, date);
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      accountService.decrementAccountBalance(accountId, amount.abs(), currency, date);
    }
  }
}
