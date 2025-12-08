package it.moneyverse.transaction.services;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.SubscriptionTestFactory;
import it.moneyverse.transaction.model.TagTestFactory;
import it.moneyverse.transaction.model.TransactionTestFactory;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.entities.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionFactoryServiceTest {

  @InjectMocks private TransactionFactoryService transactionFactoryService;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private BudgetServiceClient budgetServiceClient;
  @Mock private TagService tagService;

  @Test
  void testCreateTransaction_FromTransactionRequestItemDto() {
    UUID budgetId = UUID.randomUUID();
    TransactionRequestDto request =
        TransactionTestFactory.TransactionRequestBuilder.defaultInstance();
    TransactionRequestItemDto item = request.transactions().getFirst();

    when(budgetServiceClient.getBudgetId(item.categoryId(), item.date())).thenReturn(budgetId);
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(
            request.userId(), item.amount(), item.currency(), item.date()))
        .thenReturn(item.amount());
    when(tagService.getTagsByIds(item.tags()))
        .thenReturn(Set.of(TagTestFactory.fakeTag(request.userId())));

    Transaction result = transactionFactoryService.createTransaction(request.userId(), item);

    assertNotNull(result);
    assertEquals(request.userId(), result.getUserId());
    assertEquals(item.accountId(), result.getAccountId());
    assertEquals(item.categoryId(), result.getCategoryId());
    assertEquals(budgetId, result.getBudgetId());
    assertEquals(item.amount(), result.getAmount());
    assertEquals(item.amount(), result.getNormalizedAmount());
    assertEquals(item.currency(), result.getCurrency());
    assertEquals(item.date(), result.getDate());
    assertEquals(item.description(), result.getDescription());
    assertEquals(item.tags().size(), result.getTags().size());
  }

  @Test
  void testCreateTransaction_FromSubscription() {
    UUID budgetId = UUID.randomUUID();
    LocalDate date = RandomUtils.randomDate();
    Subscription subscription = SubscriptionTestFactory.fakeSubscription();

    when(budgetServiceClient.getBudgetId(subscription.getCategoryId(), date)).thenReturn(budgetId);
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(
            subscription.getUserId(), subscription.getAmount(), subscription.getCurrency(), date))
        .thenReturn(subscription.getAmount());

    Transaction result = transactionFactoryService.createTransaction(subscription, date);

    assertNotNull(result);
    assertEquals(subscription.getUserId(), result.getUserId());
    assertEquals(subscription.getAccountId(), result.getAccountId());
    assertEquals(subscription.getCategoryId(), result.getCategoryId());
    assertEquals(budgetId, result.getBudgetId());
    assertEquals(subscription.getAmount().abs(), result.getAmount().abs());
    assertEquals(subscription.getAmount().abs(), result.getNormalizedAmount().abs());
    assertEquals(subscription.getCurrency(), result.getCurrency());
    assertEquals(date, result.getDate());
    assertEquals(subscription.getSubscriptionName(), result.getDescription());
  }
}
