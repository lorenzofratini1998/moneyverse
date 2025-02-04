package it.moneyverse.budget.model.event;

import it.moneyverse.core.model.events.MessageEvent;
import it.moneyverse.core.utils.JsonUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.ReflectionUtils;

public class BudgetDeletionEvent implements MessageEvent<UUID, String> {

    private final UUID budgetId;
  private final UUID userId;

  public BudgetDeletionEvent(UUID budgetId, UUID userId) {
        this.budgetId = budgetId;
    this.userId = userId;
    }

    @Override
    public UUID key() {
        return budgetId;
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
