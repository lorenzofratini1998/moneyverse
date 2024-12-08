package it.moneyverse.account.model.Event;

import it.moneyverse.core.utils.JsonUtils;
import java.util.UUID;

public record AccountDeletionEvent(UUID accountId, String username) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
