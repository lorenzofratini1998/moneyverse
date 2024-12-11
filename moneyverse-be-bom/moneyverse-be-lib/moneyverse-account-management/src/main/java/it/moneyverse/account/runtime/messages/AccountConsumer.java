package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.model.event.UserDeletionEvent;
import it.moneyverse.account.services.AccountService;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.core.utils.JsonUtils;

import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AccountConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountConsumer.class);
  private final AccountService accountService;
  private final UserServiceClient userServiceClient;

  public AccountConsumer(
          AccountService accountService,
          UserServiceClient userServiceClient
  ) {
    this.accountService = accountService;
    this.userServiceClient = userServiceClient;
  }

  @RetryableTopic
  @KafkaListener(
      topics = UserDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onMessage(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    LOGGER.info("Received event: {} from topic: {}", record.value(), topic);
    UserDeletionEvent event = JsonUtils.fromJson(record.value(), UserDeletionEvent.class);
    checkIfUserExists(event.username());
    accountService.deleteAccountsByUsername(event.username());
  }

  @DltHandler
  public void onError(
      ConsumerRecord<UUID, String> record,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Exception exception) {
    LOGGER.error(
        "Error received event: {} from topic: {}. Error: {}",
        record.value(),
        topic,
        exception.getMessage());
  }

  private void checkIfUserExists(String username) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(username))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(username));
    }
  }
}
