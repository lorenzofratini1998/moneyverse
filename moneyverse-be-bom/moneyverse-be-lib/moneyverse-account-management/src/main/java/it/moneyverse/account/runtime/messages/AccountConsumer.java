package it.moneyverse.account.runtime.messages;

import static it.moneyverse.core.utils.ConsumerUtils.logMessage;

import it.moneyverse.account.services.AccountService;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import java.math.BigDecimal;
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
public class AccountConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountConsumer.class);
  private final AccountService accountService;

  public AccountConsumer(AccountService accountService) {
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
    UserDeletionEvent event = JsonUtils.fromJson(record.value(), UserDeletionEvent.class);
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
    accountService.incrementAccountBalance(event.getAccountId(), event.getAmount());
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
    accountService.decrementAccountBalance(event.getAccountId(), event.getAmount());
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
    BigDecimal previousAmount = event.getPreviousAmount();
    BigDecimal amount = event.getAmount() != null ? event.getAmount() : BigDecimal.ZERO;
    boolean isAccountChanged =
        event.getPreviousAccountId() != null
            && !event.getPreviousAccountId().equals(event.getAccountId());

    if (isAccountChanged) {
      if (previousAmount == null) {
        applyTransaction(event.getPreviousAccountId(), amount.negate());
        applyTransaction(event.getAccountId(), amount);
      } else {
        applyTransaction(event.getPreviousAccountId(), previousAmount.negate());
        applyTransaction(event.getAccountId(), event.getAmount());
      }
    } else {
      applyTransaction(
          event.getAccountId(),
          amount.subtract(previousAmount != null ? previousAmount : BigDecimal.ZERO));
    }
  }

  private void applyTransaction(UUID accountId, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      accountService.incrementAccountBalance(accountId, amount);
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      accountService.decrementAccountBalance(accountId, amount.abs());
    }
  }
}
