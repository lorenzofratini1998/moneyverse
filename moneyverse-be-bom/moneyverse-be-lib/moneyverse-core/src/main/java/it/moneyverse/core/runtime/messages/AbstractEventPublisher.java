package it.moneyverse.core.runtime.messages;

import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.OutboxEvent;
import java.util.UUID;

public abstract class AbstractEventPublisher<E> {

  protected OutboxEvent buildOutboxEvent(
      UUID aggregateId,
      String topic,
      AggregateTypeEnum aggregateType,
      EventTypeEnum eventType,
      E eventPayload) {
    OutboxEvent event = new OutboxEvent();
    event.setAggregateId(aggregateId);
    event.setTopic(topic);
    event.setAggregateType(aggregateType);
    event.setEventType(eventType);
    event.setPayload(eventPayload.toString());
    return event;
  }
}
