package it.moneyverse.transaction.runtime.batch;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createSubscription;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.transaction.model.entities.Subscription;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionProcessorTest {

  @InjectMocks private SubscriptionProcessor subscriptionProcessor;

  @Test
  void testProcess() throws Exception {
    Subscription subscription = createSubscription();

    List<Subscription> result = subscriptionProcessor.process(List.of(subscription));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.getFirst().getTransactions().size());
  }
}
