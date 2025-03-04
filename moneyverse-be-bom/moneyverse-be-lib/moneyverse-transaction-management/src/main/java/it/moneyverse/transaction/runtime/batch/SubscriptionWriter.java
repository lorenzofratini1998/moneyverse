package it.moneyverse.transaction.runtime.batch;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionWriter implements ItemWriter<List<Subscription>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionWriter.class);
  private final SubscriptionRepository subscriptionRepository;
  private final TransactionEventPublisher transactionEventPublisher;

  public SubscriptionWriter(
      SubscriptionRepository subscriptionRepository,
      TransactionEventPublisher transactionEventPublisher) {
    this.subscriptionRepository = subscriptionRepository;
    this.transactionEventPublisher = transactionEventPublisher;
  }

  @Override
  public void write(Chunk<? extends List<Subscription>> chunk) {
    LOGGER.info("Adding scheduled subscription transactions to batch");
    List<Subscription> subscriptions = chunk.getItems().stream().flatMap(List::stream).toList();
    if (!subscriptions.isEmpty()) {
      saveSubscriptions(subscriptions);
      publishEvents(subscriptions);
    }
  }

  private void saveSubscriptions(List<Subscription> subscriptions) {
    subscriptionRepository.saveAll(subscriptions);
  }

  private void publishEvents(List<Subscription> subscriptions) {
    List<Transaction> batchTransactions =
        subscriptions.stream()
            .flatMap(subscription -> subscription.getTransactions().stream())
            .filter(transaction -> transaction.getDate().equals(LocalDate.now()))
            .toList();
    transactionEventPublisher.publishEvent(batchTransactions, EventTypeEnum.CREATE);
  }
}
