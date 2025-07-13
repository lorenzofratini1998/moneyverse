package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.model.events.BudgetEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.model.repositories.ProcessedEventRepository;
import it.moneyverse.core.runtime.messages.AbstractConsumer;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.transaction.services.TransactionService;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer extends AbstractConsumer {

  private final TransactionService transactionService;

  public TransactionConsumer(
      TransactionService transactionService, ProcessedEventRepository processedEventRepository) {
    super(processedEventRepository);
    this.transactionService = transactionService;
  }

  @RetryableTopic
  @KafkaListener(
      topics = UserDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onUserDeletionEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    UserEvent event = JsonUtils.fromJson(record.value(), UserEvent.class);
    transactionService.deleteAllTransactionsByUserId(event.getUserId());
  }

  @RetryableTopic
  @KafkaListener(
      topics = AccountDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onAccountDeletionEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    AccountEvent event = JsonUtils.fromJson(record.value(), AccountEvent.class);
    if (!eventAlreadyProcessed(record.key()) && event.getAccountId() != null) {
      transactionService.deleteAllTransactionsByAccountId(event.getAccountId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @RetryableTopic
  @KafkaListener(
      topics = CategoryDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onCategoryDeletionEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    CategoryEvent event = JsonUtils.fromJson(record.value(), CategoryEvent.class);
    if (!eventAlreadyProcessed(record.key()) && event.getCategoryId() != null) {
      transactionService.removeCategoryFromTransactions(event.getCategoryId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @RetryableTopic
  @KafkaListener(
      topics = BudgetDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onCategoryBudgetEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    BudgetEvent event = JsonUtils.fromJson(record.value(), BudgetEvent.class);
    if (!eventAlreadyProcessed(record.key()) && event.getBudgetId() != null) {
      transactionService.removeBudgetFromTransactions(event.getBudgetId());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }
}
