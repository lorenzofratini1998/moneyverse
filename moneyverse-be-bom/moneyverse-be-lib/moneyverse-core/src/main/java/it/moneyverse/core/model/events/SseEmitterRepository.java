package it.moneyverse.core.model.events;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRepository {
  private final Map<UUID, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

  public void add(UUID userId, SseEmitter emitter) {
    emitters.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(emitter);
  }

  public void remove(UUID userId, SseEmitter emitter) {
    Set<SseEmitter> emitters = this.emitters.get(userId);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        this.emitters.remove(userId);
      }
    }
  }

  public Set<SseEmitter> get(UUID userId) {
    return this.emitters.get(userId);
  }
}
