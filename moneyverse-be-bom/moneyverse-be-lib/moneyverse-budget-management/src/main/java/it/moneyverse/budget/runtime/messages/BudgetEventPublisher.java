package it.moneyverse.budget.runtime.messages;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.BudgetEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BudgetEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public BudgetEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishEvent(Category category, EventTypeEnum eventType) {
    CategoryEvent event =
        CategoryEvent.builder()
            .withCategoryId(category.getCategoryId())
            .withParentId(
                category.getParentCategory() != null
                    ? category.getParentCategory().getCategoryId()
                    : null)
            .withUserId(category.getUserId())
            .withCategoryName(category.getCategoryName())
            .withEventType(eventType)
            .build();
    eventPublisher.publishEvent(event);
  }

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
}
