package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.events.TopicResolver;
import it.moneyverse.core.model.events.TransactionEvent;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class TransactionTopicResolver implements TopicResolver<TransactionEvent> {

  @Override
  public String resolveTopic(TransactionEvent event) {
    EventTypeEnum eventType =
        Objects.requireNonNull(event.getEventType(), "The event type cannot be null.");
    return switch (eventType) {
      case CREATE -> TransactionCreationTopic.TOPIC;
      case UPDATE -> TransactionUpdateTopic.TOPIC;
      case DELETE -> TransactionDeletionTopic.TOPIC;
    };
  }
}
