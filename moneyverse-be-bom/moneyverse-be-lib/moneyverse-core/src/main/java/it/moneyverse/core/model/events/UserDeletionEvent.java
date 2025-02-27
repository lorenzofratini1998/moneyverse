package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class UserDeletionEvent extends AbstractEvent {

  private final UUID userId;

  @JsonCreator
  public UserDeletionEvent(@JsonProperty("userId") UUID userId) {
    this.userId = userId;
  }

  public UUID getUserId() {
    return userId;
  }

  @Override
  public UUID key() {
    return userId;
  }
}
