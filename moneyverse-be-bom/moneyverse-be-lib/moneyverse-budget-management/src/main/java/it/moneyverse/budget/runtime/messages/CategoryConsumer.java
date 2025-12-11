package it.moneyverse.budget.runtime.messages;

import static it.moneyverse.core.utils.ConsumerUtils.logMessage;

import it.moneyverse.budget.services.CategoryService;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.utils.JsonUtils;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class CategoryConsumer {

  private final CategoryService categoryService;

  public CategoryConsumer(CategoryService categoryService) {
    this.categoryService = categoryService;
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
  public void onUserDeletionEvent(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    UserEvent event = JsonUtils.fromJson(record.value(), UserEvent.class);
    categoryService.deleteCategoriesByUserId(event.key());
  }
}
