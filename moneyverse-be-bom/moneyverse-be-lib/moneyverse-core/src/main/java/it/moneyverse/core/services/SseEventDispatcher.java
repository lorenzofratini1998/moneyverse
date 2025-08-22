package it.moneyverse.core.services;

import it.moneyverse.core.model.events.SseEmitterRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEventDispatcher {
  private static final Logger logger = LoggerFactory.getLogger(SseEventDispatcher.class);
  private final SseEmitterRepository sseEmitterRepository;

  public SseEventDispatcher(SseEmitterRepository sseEmitterRepository) {
    this.sseEmitterRepository = sseEmitterRepository;
  }

  public void send(UUID userId, String eventType, Object data) {
    Set<SseEmitter> emitters = sseEmitterRepository.get(userId);
    if (emitters != null && !emitters.isEmpty()) {
      List<SseEmitter> deadEmitters = new ArrayList<>();

      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event().name(eventType).data(data));
          logger.debug("Successfully sent {} event to user {}", eventType, userId);
        } catch (IOException e) {
          logger.debug(
              "Failed to send {} event to user {}. Connection appears to be closed: {}",
              eventType,
              userId,
              e.getMessage());
          deadEmitters.add(emitter);
        } catch (Exception e) {
          logger.warn(
              "Unexpected error sending {} event to user {}: {}",
              eventType,
              userId,
              e.getMessage(),
              e);
          deadEmitters.add(emitter);
        }
      }
      if (!deadEmitters.isEmpty()) {
        deadEmitters.forEach(emitter -> sseEmitterRepository.remove(userId, emitter));
        logger.debug("Removed {} dead emitters for user {}", deadEmitters.size(), userId);
      }
    } else {
      logger.debug(
          "No active emitters found for user {} when trying to send {} event", userId, eventType);
    }
  }
}
