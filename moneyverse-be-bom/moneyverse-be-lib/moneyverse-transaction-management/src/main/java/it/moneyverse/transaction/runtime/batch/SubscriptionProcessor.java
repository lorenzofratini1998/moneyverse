package it.moneyverse.transaction.runtime.batch;

import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.services.SubscriptionService;
import it.moneyverse.transaction.services.TransactionFactoryService;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionProcessor
    implements ItemProcessor<List<Subscription>, List<Subscription>> {

  private static final String BATCH_JOB = "BATCH_JOB";

  private final SubscriptionService subscriptionService;
  private final TransactionFactoryService transactionFactoryService;

  public SubscriptionProcessor(
      SubscriptionService subscriptionService,
      TransactionFactoryService transactionFactoryService) {
    this.subscriptionService = subscriptionService;
    this.transactionFactoryService = transactionFactoryService;
  }

  @Override
  public List<Subscription> process(@Nonnull List<Subscription> subscriptions) {
    subscriptions.forEach(this::processSubscription);
    return subscriptions;
  }

  private void processSubscription(Subscription subscription) {
    LocalDate nextExecutionDate = subscriptionService.calculateNextExecutionDate(subscription);
    subscription.setNextExecutionDate(nextExecutionDate);
    if (shouldDeactivateSubscription(subscription)) {
      subscription.setActive(false);
    }
    Transaction transaction =
        transactionFactoryService.createTransaction(subscription, nextExecutionDate);
    transaction.setCreatedBy(BATCH_JOB);
    transaction.setUpdatedBy(BATCH_JOB);
    subscription.addTransaction(transaction);
  }

  private boolean shouldDeactivateSubscription(Subscription subscription) {
    return subscription.getNextExecutionDate() == null
        || (subscription.getEndDate() != null
            && subscription.getNextExecutionDate().isAfter(subscription.getEndDate()));
  }
}
