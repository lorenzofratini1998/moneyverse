package it.moneyverse.core.model.events;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.utils.JsonUtils;
import java.util.UUID;

public abstract class AbstractEvent implements MessageEvent<UUID, String> {

  protected final EventTypeEnum eventType;

  protected AbstractEvent(AbstractBuilder<?, ?> builder) {
    this.eventType = builder.eventType;
  }

  public abstract static class AbstractBuilder<
      T extends AbstractEvent, B extends AbstractBuilder<T, B>> {
    private EventTypeEnum eventType;

    public B withEventType(EventTypeEnum eventType) {
      this.eventType = eventType;
      return self();
    }

    protected abstract B self();

    public abstract T build();
  }

  public EventTypeEnum getEventType() {
    return eventType;
  }

  @Override
  public String value() {
    return JsonUtils.toJson(this);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
