package it.moneyverse.budget.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.model.events.TopicResolver;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CategoryTopicResolver implements TopicResolver<CategoryEvent> {

  @Override
  public String resolveTopic(CategoryEvent event) {
    EventTypeEnum eventType =
        Objects.requireNonNull(event.getEventType(), "The event type cannot be null.");
    if (eventType == EventTypeEnum.DELETE) {
      return CategoryDeletionTopic.TOPIC;
    }
    throw new IllegalStateException("Unexpected value %s".formatted(eventType.name()));
  }
}
