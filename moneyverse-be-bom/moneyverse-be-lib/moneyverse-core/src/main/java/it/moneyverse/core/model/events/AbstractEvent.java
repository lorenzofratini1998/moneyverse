package it.moneyverse.core.model.events;

import it.moneyverse.core.utils.JsonUtils;
import java.util.UUID;

public abstract class AbstractEvent implements MessageEvent<UUID, String> {

  @Override
  public String value() {
    return JsonUtils.toJson(this);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
