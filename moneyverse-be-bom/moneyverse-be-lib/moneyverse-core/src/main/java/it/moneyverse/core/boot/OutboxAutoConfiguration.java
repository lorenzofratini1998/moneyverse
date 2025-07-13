package it.moneyverse.core.boot;

import it.moneyverse.core.model.repositories.OutboxEventRepository;
import it.moneyverse.core.runtime.messages.OutboxPoller;
import it.moneyverse.core.runtime.messages.OutboxProcessor;
import it.moneyverse.core.runtime.messages.OutboxPublisher;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class OutboxAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxAutoConfiguration.class);

  public OutboxAutoConfiguration() {
    LOGGER.info("Starting to load beans from {}", OutboxAutoConfiguration.class.getName());
  }

  @Bean
  public OutboxPublisher outboxPublisher(KafkaTemplate<UUID, String> kafkaTemplate) {
    return new OutboxPublisher(kafkaTemplate);
  }

  @Bean
  public OutboxPoller outboxPoller(OutboxEventRepository outboxEventRepository) {
    return new OutboxPoller(outboxEventRepository);
  }

  @Bean
  public OutboxProcessor outboxProcessor(
      OutboxPoller outboxPoller,
      OutboxPublisher outboxPublisher,
      OutboxEventRepository outboxEventRepository) {
    return new OutboxProcessor(outboxPoller, outboxPublisher, outboxEventRepository);
  }
}
