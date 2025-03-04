package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.AccountEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public AccountEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishEvent(Account account, EventTypeEnum eventType) {
    AccountEvent event =
        AccountEvent.builder()
            .withAccountId(account.getAccountId())
            .withUserId(account.getUserId())
            .withEventType(eventType)
            .build();
    eventPublisher.publishEvent(event);
  }
}
