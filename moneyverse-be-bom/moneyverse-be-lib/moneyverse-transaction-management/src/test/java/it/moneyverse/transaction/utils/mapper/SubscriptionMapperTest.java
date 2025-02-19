package it.moneyverse.transaction.utils.mapper;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createSubscription;
import static it.moneyverse.transaction.utils.TransactionTestUtils.createSubscriptionRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import org.junit.jupiter.api.Test;

class SubscriptionMapperTest {

  @Test
  void testToSubscription_NullSubscriptionRequest() {
    assertNull(SubscriptionMapper.toSubscription(null));
  }

  @Test
  void testToSubscription_SubscriptionRequest() {
    SubscriptionRequestDto request =
        createSubscriptionRequest(
            RandomUtils.randomUUID(), RandomUtils.randomLocalDate(2024, 2025));

    Subscription subscription = SubscriptionMapper.toSubscription(request);

    assertEquals(request.userId(), subscription.getUserId());
    assertEquals(request.accountId(), subscription.getAccountId());
    assertEquals(request.categoryId(), subscription.getCategoryId());
    assertEquals(request.amount(), subscription.getAmount());
    assertEquals(request.currency(), subscription.getCurrency());
    assertEquals(request.subscriptionName(), subscription.getSubscriptionName());
    assertEquals(request.recurrence().recurrenceRule(), subscription.getRecurrenceRule());
    assertEquals(request.recurrence().startDate(), subscription.getStartDate());
    assertEquals(request.recurrence().endDate(), subscription.getEndDate());
  }

  @Test
  void testToSubscriptionDto_NullSubscription() {
    assertNull(SubscriptionMapper.toSubscriptionDto(null));
  }

  @Test
  void testToSubscriptionDto_Subscription() {
    Subscription subscription = createSubscription();

    SubscriptionDto subscriptionDto = SubscriptionMapper.toSubscriptionDto(subscription);

    assertEquals(subscription.getSubscriptionId(), subscriptionDto.getSubscriptionId());
    assertEquals(subscription.getUserId(), subscriptionDto.getUserId());
    assertEquals(subscription.getAccountId(), subscriptionDto.getAccountId());
    assertEquals(subscription.getCategoryId(), subscriptionDto.getCategoryId());
    assertEquals(subscription.getAmount(), subscriptionDto.getAmount());
    assertEquals(subscription.getCurrency(), subscriptionDto.getCurrency());
    assertEquals(subscription.getSubscriptionName(), subscriptionDto.getSubscriptionName());
    assertEquals(subscription.getRecurrenceRule(), subscriptionDto.getRecurrenceRule());
    assertEquals(subscription.getStartDate(), subscriptionDto.getStartDate());
    assertEquals(subscription.getEndDate(), subscriptionDto.getEndDate());
  }
}
