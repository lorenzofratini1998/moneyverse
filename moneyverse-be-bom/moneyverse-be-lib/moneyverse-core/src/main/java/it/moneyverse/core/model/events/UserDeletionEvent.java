package it.moneyverse.core.model.events;

import java.util.UUID;

public class UserDeletionEvent implements MessageEvent<UUID, String> {

  private UUID userId;

  public UserDeletionEvent() {}

  public UserDeletionEvent(UUID userId) {
    this.userId = userId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
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
