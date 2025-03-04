package it.moneyverse.transaction.runtime.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.SubscriptionTestFactory;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.services.SubscriptionService;
import it.moneyverse.transaction.services.TransactionFactoryService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionProcessorTest {

  @InjectMocks private SubscriptionProcessor subscriptionProcessor;
  @Mock private SubscriptionService subscriptionService;
  @Mock private TransactionFactoryService transactionFactoryService;

  @Test
  void testProcess(@Mock Transaction transaction) {
    Subscription subscription = SubscriptionTestFactory.fakeSubscription();
    when(subscriptionService.calculateNextExecutionDate(subscription))
        .thenReturn(RandomUtils.randomDate());
    when(transactionFactoryService.createTransaction(eq(subscription), any(LocalDate.class)))
        .thenReturn(transaction);

    List<Subscription> result = subscriptionProcessor.process(List.of(subscription));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.getFirst().getTransactions().size());
  }
}
