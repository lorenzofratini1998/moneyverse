package it.moneyverse.budget.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.events.BudgetEvent;
import it.moneyverse.core.model.events.TopicResolver;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BudgetTopicResolver implements TopicResolver<BudgetEvent> {

  @Override
  public String resolveTopic(BudgetEvent event) {
    EventTypeEnum eventType =
        Objects.requireNonNull(event.getEventType(), "The event type cannot be null.");
    if (eventType == EventTypeEnum.DELETE) {
      return BudgetDeletionTopic.TOPIC;
    }
    throw new IllegalStateException("Unexpected value %s".formatted(eventType.name()));
  }
}
