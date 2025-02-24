package it.moneyverse.transaction.runtime.batch;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createSubscription;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.entities.Subscription;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionProcessorTest {

  @InjectMocks private SubscriptionProcessor subscriptionProcessor;
  @Mock private CurrencyServiceClient currencyServiceClient;

  @Test
  void testProcess() throws Exception {
    Subscription subscription = createSubscription();
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(any(), any(), any(), any()))
        .thenReturn(RandomUtils.randomBigDecimal());

    List<Subscription> result = subscriptionProcessor.process(List.of(subscription));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.getFirst().getTransactions().size());
  }
}
