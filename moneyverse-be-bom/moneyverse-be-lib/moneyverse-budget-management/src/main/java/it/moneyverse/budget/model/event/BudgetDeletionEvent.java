package it.moneyverse.budget.model.event;

import it.moneyverse.core.model.events.MessageEvent;
import it.moneyverse.core.utils.JsonUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BudgetDeletionEvent implements MessageEvent<UUID, String> {

    private final UUID budgetId;
    private final String username;

    public BudgetDeletionEvent(UUID budgetId, String username) {
        this.budgetId = budgetId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }
}
