package it.moneyverse.core.services;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SseEventService {
  private final SseEventDispatcher sseEventDispatcher;

  public SseEventService(SseEventDispatcher sseEventDispatcher) {
    this.sseEventDispatcher = sseEventDispatcher;
  }

  public void publishEvent(UUID userId, String eventType, Object data) {
    sseEventDispatcher.send(userId, eventType, data);
  }
}
