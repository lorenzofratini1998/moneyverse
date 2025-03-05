package it.moneyverse.account.runtime.messages;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.model.events.TopicResolver;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AccountTopicResolver implements TopicResolver<AccountEvent> {

  @Override
  public String resolveTopic(AccountEvent event) {
    EventTypeEnum eventType =
        Objects.requireNonNull(event.getEventType(), "The event type cannot be null.");
    if (eventType == EventTypeEnum.DELETE) {
      return AccountDeletionTopic.TOPIC;
    }
    throw new IllegalStateException("Unexpected value %s".formatted(eventType.name()));
  }
}
