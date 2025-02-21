package it.moneyverse.transaction.runtime.batch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionReaderTest {
  @Mock SubscriptionRepository subscriptionRepository;
  @InjectMocks private SubscriptionReader subscriptionReader;

  @Test
  void testRead(@Mock Subscription subscription) {
    when(subscriptionRepository.findSubscriptionByNextExecutionDateAndIsActive(
            LocalDate.now(), true))
        .thenReturn(List.of(subscription));

    List<Subscription> result = subscriptionReader.read();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(subscriptionRepository, times(1))
        .findSubscriptionByNextExecutionDateAndIsActive(LocalDate.now(), true);
  }
}
