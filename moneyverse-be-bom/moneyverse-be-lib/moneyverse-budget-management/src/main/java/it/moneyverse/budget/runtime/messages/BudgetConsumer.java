package it.moneyverse.budget.runtime.messages;

import it.moneyverse.budget.services.BudgetService;
import it.moneyverse.core.model.beans.UserCreationTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.UserCreationEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static it.moneyverse.core.utils.ConsumerUtils.logMessage;

@Component
public class BudgetConsumer {

  private final BudgetService budgetService;

  public BudgetConsumer(BudgetService budgetService) {
    this.budgetService = budgetService;
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
    budgetService.deleteAllBudgets(event.username());
  }

  @RetryableTopic
  @KafkaListener(
      topics = UserCreationTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onUserCreationEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    UserCreationEvent event = JsonUtils.fromJson(record.value(), UserCreationEvent.class);
    budgetService.createDefaultBudgets(event.username(), event.currency());
  }


}
