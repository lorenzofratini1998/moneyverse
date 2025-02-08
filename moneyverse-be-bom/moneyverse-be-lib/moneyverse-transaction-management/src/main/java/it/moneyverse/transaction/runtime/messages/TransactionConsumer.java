package it.moneyverse.transaction.runtime.messages;

import static it.moneyverse.core.utils.ConsumerUtils.logMessage;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.AccountDeletionEvent;
import it.moneyverse.core.model.events.BudgetDeletionEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
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
public class TransactionConsumer {

  private final TransactionService transactionService;

  public TransactionConsumer(TransactionService transactionService) {
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
    UserDeletionEvent event = JsonUtils.fromJson(record.value(), UserDeletionEvent.class);
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
    AccountDeletionEvent event = JsonUtils.fromJson(record.value(), AccountDeletionEvent.class);
    transactionService.deleteAllTransactionsByAccountId(event.accountId());
  }

  @RetryableTopic
  @KafkaListener(
      topics = CategoryDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onBudgetDeletionEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    BudgetDeletionEvent event = JsonUtils.fromJson(record.value(), BudgetDeletionEvent.class);
    transactionService.removeBudgetFromTransactions(event.budgetId());
  }
}
