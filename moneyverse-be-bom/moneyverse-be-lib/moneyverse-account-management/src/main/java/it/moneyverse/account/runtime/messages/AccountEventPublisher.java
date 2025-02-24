package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.event.AccountDeletionEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public AccountEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishEvent(Account account) {
    AccountDeletionEvent event =
        new AccountDeletionEvent(account.getAccountId(), account.getUserId());
    eventPublisher.publishEvent(event);
  }
}
