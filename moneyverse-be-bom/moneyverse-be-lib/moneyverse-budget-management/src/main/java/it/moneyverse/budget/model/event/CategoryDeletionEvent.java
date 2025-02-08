package it.moneyverse.budget.model.event;

import it.moneyverse.core.model.events.MessageEvent;
import it.moneyverse.core.utils.JsonUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.ReflectionUtils;

public class CategoryDeletionEvent implements MessageEvent<UUID, String> {

  private final UUID categoryId;
  private final UUID userId;

  public CategoryDeletionEvent(UUID categoryId, UUID userId) {
    this.categoryId = categoryId;
    this.userId = userId;
    }

    @Override
    public UUID key() {
    return categoryId;
    }

    @Override
    public String value() {
        Map<String, Object> payload = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            payload.put(field.getName(), ReflectionUtils.getField(field, this));
        }
        return JsonUtils.toJson(payload);
    }

  public UUID getUserId() {
    return userId;
    }
}
