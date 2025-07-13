package it.moneyverse.budget.runtime.messages;

import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.entities.OutboxEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.model.repositories.OutboxEventRepository;
import it.moneyverse.core.runtime.messages.AbstractEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CategoryEventPublisher extends AbstractEventPublisher<CategoryEvent> {

  private final ApplicationEventPublisher eventPublisher;
  private final CategoryTopicResolver categoryTopicResolver;
  private final OutboxEventRepository outboxEventRepository;

  public CategoryEventPublisher(
      ApplicationEventPublisher eventPublisher,
      CategoryTopicResolver categoryTopicResolver,
      OutboxEventRepository outboxEventRepository) {
    this.eventPublisher = eventPublisher;
    this.categoryTopicResolver = categoryTopicResolver;
    this.outboxEventRepository = outboxEventRepository;
  }

  @Deprecated
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

  public void publish(Category category, EventTypeEnum eventType) {
    OutboxEvent event = createEvent(category, eventType);
    outboxEventRepository.save(event);
  }

  private OutboxEvent createEvent(Category category, EventTypeEnum eventType) {
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
    return buildOutboxEvent(
        event.getCategoryId(),
        categoryTopicResolver.resolveTopic(event),
        AggregateTypeEnum.CATEGORY,
        eventType,
        event);
  }
}
