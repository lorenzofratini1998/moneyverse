package it.moneyverse.budget.runtime.messages;

import it.moneyverse.core.model.events.BudgetEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.services.MessageProducer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Deprecated
@Component
public class BudgetEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetEventListener.class);
  private final MessageProducer<UUID, String> messageProducer;
  private final CategoryTopicResolver categoryTopicResolver;
  private final BudgetTopicResolver budgetTopicResolver;

  public BudgetEventListener(
      MessageProducer<UUID, String> messageProducer,
      CategoryTopicResolver categoryTopicResolver,
      BudgetTopicResolver budgetTopicResolver) {
    this.messageProducer = messageProducer;
    this.categoryTopicResolver = categoryTopicResolver;
    this.budgetTopicResolver = budgetTopicResolver;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCategoryEvent(CategoryEvent event) {
    LOGGER.info("Sending category event: {}", event);
    messageProducer.send(event, categoryTopicResolver.resolveTopic(event));
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBudgetEvent(BudgetEvent event) {
    LOGGER.info("Sending budget event: {}", event);
    messageProducer.send(event, budgetTopicResolver.resolveTopic(event));
  }
}
