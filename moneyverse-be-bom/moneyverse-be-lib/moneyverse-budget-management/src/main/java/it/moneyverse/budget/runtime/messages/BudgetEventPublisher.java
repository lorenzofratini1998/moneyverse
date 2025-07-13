package it.moneyverse.budget.runtime.messages;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.events.BudgetEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import it.moneyverse.core.runtime.messages.AbstractEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BudgetEventPublisher extends AbstractEventPublisher<BudgetEvent> {

  private final ApplicationEventPublisher eventPublisher;
  private final BudgetTopicResolver budgetTopicResolver;
  private final OutboxEventRepository outboxEventRepository;

  public BudgetEventPublisher(
      ApplicationEventPublisher eventPublisher,
      BudgetTopicResolver budgetTopicResolver,
      OutboxEventRepository outboxEventRepository) {
    this.eventPublisher = eventPublisher;
    this.budgetTopicResolver = budgetTopicResolver;
    this.outboxEventRepository = outboxEventRepository;
  }

  @Deprecated
  public void publishEvent(Budget budget, EventTypeEnum eventType) {
    BudgetEvent event =
        BudgetEvent.builder()
            .withBudgetId(budget.getBudgetId())
            .withCategoryId(budget.getCategory().getCategoryId())
            .withStartDate(budget.getStartDate())
            .withEndDate(budget.getEndDate())
            .withAmount(budget.getAmount())
            .withBudgetLimit(budget.getBudgetLimit())
            .withCurrency(budget.getCurrency())
            .withEventType(eventType)
            .build();
    eventPublisher.publishEvent(event);
  }

  public void publish(Budget budget, EventTypeEnum eventType) {
    OutboxEvent event = createEvent(budget, eventType);
    outboxEventRepository.save(event);
  }

  private OutboxEvent createEvent(Budget budget, EventTypeEnum eventType) {
    BudgetEvent event =
        BudgetEvent.builder()
            .withBudgetId(budget.getBudgetId())
            .withCategoryId(budget.getCategory().getCategoryId())
            .withStartDate(budget.getStartDate())
            .withEndDate(budget.getEndDate())
            .withAmount(budget.getAmount())
            .withBudgetLimit(budget.getBudgetLimit())
            .withCurrency(budget.getCurrency())
            .withEventType(eventType)
            .build();
    return buildOutboxEvent(
        event.getBudgetId(),
        budgetTopicResolver.resolveTopic(event),
        AggregateTypeEnum.BUDGET,
        eventType,
        event);
  }
}
