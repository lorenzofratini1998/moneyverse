package it.moneyverse.account.model.event;

import it.moneyverse.core.model.events.MessageEvent;
import it.moneyverse.core.utils.JsonUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountDeletionEvent implements MessageEvent<UUID, String> {

  private final UUID accountId;
  private final String username;

  public AccountDeletionEvent(UUID accountId, String username) {
      this.accountId = accountId;
      this.username = username;
  }

  @Override
  public UUID key() {
    return accountId;
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
