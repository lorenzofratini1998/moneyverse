package it.moneyverse.transaction.runtime.batch;

import static it.moneyverse.transaction.utils.SubscriptionUtils.*;

import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionProcessor
    implements ItemProcessor<List<Subscription>, List<Subscription>> {

  @Override
  public List<Subscription> process(@Nonnull List<Subscription> subscriptions) {
    for (Subscription subscription : subscriptions) {
      LocalDate nextExecutionDate = calculateNextExecutionDate(subscription);
      subscription.setNextExecutionDate(nextExecutionDate);
      if (subscription.getEndDate() != null
          && nextExecutionDate != null
          && nextExecutionDate.isAfter(subscription.getEndDate())) {
        subscription.setActive(false);
      }
      Transaction transaction = createSubscriptionTransaction(subscription, LocalDate.now());
      transaction.setCreatedBy(BATCH_JOB);
      transaction.setUpdatedBy(BATCH_JOB);
      subscription.addTransaction(transaction);
    }
    return subscriptions;
  }
}
