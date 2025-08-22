package it.moneyverse.core.boot;

import it.moneyverse.core.model.events.SseEmitterRepository;
import it.moneyverse.core.services.SseEventDispatcher;
import it.moneyverse.core.services.SseEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(SseAutoConfiguration.class);

  public SseAutoConfiguration() {
    LOGGER.info("Starting to load beans from {}", SseAutoConfiguration.class.getName());
  }

  @Bean
  public SseEmitterRepository sseEmitterRepository() {
    return new SseEmitterRepository();
  }

  @Bean
  public SseEventDispatcher sseEventDispatcher(SseEmitterRepository sseEmitterRepository) {
    return new SseEventDispatcher(sseEmitterRepository);
  }

  @Bean
  public SseEventService sseEventService(SseEventDispatcher sseEventDispatcher) {
    return new SseEventService(sseEventDispatcher);
  }
}
