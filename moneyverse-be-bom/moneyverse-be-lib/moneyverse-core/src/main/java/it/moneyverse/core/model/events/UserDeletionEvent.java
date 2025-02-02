package it.moneyverse.core.model.events;

import java.util.UUID;

public class UserDeletionEvent implements MessageEvent<UUID, String> {

  private final UUID userId;

  public UserDeletionEvent(UUID userId) {
    this.userId = userId;
  }

  @Override
  public UUID key() {
    return userId;
  }

  @Override
  public String value() {
    return userId.toString();
  }
}
