package it.moneyverse.transaction.runtime.batch;

import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionReader implements ItemReader<List<Subscription>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionReader.class);

  private final SubscriptionRepository subscriptionRepository;

  private boolean batchJobState = false;

  public SubscriptionReader(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @Override
  public List<Subscription> read() {
    if (!batchJobState) {
      LocalDate today = LocalDate.now();
      LOGGER.info("Reading subscriptions from batch for date: {}", today);
      List<Subscription> subscriptions =
          subscriptionRepository.findSubscriptionByNextExecutionDateAndIsActive(today, true);
      batchJobState = true;
      return subscriptions;
    }
    return null;
  }
}
