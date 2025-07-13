package it.moneyverse.account.runtime.messages;

import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import it.moneyverse.core.runtime.messages.AbstractEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountEventPublisher extends AbstractEventPublisher<AccountEvent> {

  private final ApplicationEventPublisher eventPublisher;
  private final AccountTopicResolver accountTopicResolver;
  private final OutboxEventRepository outboxEventRepository;

  public AccountEventPublisher(
      ApplicationEventPublisher eventPublisher,
      AccountTopicResolver accountTopicResolver,
      OutboxEventRepository outboxEventRepository) {
    this.eventPublisher = eventPublisher;
    this.accountTopicResolver = accountTopicResolver;
    this.outboxEventRepository = outboxEventRepository;
  }

  @Deprecated
  public void publishEvent(Account account, EventTypeEnum eventType) {
    AccountEvent event =
        AccountEvent.builder()
            .withAccountId(account.getAccountId())
            .withUserId(account.getUserId())
            .withEventType(eventType)
            .build();
    eventPublisher.publishEvent(event);
  }

  public void publish(Account account, EventTypeEnum eventType) {
    OutboxEvent event = createEvent(account, eventType);
    outboxEventRepository.save(event);
  }

  private OutboxEvent createEvent(Account account, EventTypeEnum eventType) {
    AccountEvent accountEvent =
        AccountEvent.builder()
            .withAccountId(account.getAccountId())
            .withUserId(account.getUserId())
            .withEventType(eventType)
            .build();
    return buildOutboxEvent(
        account.getAccountId(),
        accountTopicResolver.resolveTopic(accountEvent),
        AggregateTypeEnum.ACCOUNT,
        eventType,
        accountEvent);
  }
}
