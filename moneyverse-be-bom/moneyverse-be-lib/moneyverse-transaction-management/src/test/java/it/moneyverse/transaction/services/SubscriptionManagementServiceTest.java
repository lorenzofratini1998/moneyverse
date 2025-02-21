package it.moneyverse.transaction.services;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createSubscriptionRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.SubscriptionMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionManagementServiceTest {

  @InjectMocks private SubscriptionManagementService subscriptionManagementService;

  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private AccountServiceClient accountServiceClient;
  @Mock private BudgetServiceClient budgetServiceClient;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private TransactionEventPublisher transactionEventPublisher;
  private MockedStatic<SubscriptionMapper> subscriptionMapper;

  @BeforeEach
  void setUp() {
    subscriptionMapper = Mockito.mockStatic(SubscriptionMapper.class);
  }

  @AfterEach
  void tearDown() {
    subscriptionMapper.close();
  }

  @Test
  void givenSubscriptionRequest_WhenCreateSubscription_ThenReturnSubscription(
      @Mock Subscription subscription, @Mock SubscriptionDto subscriptionDto) {
    UUID userId = RandomUtils.randomUUID();
    LocalDate startDate = LocalDate.now().plusMonths(1);
    SubscriptionRequestDto request = createSubscriptionRequest(userId, startDate);
    Mockito.doNothing().when(accountServiceClient).checkIfAccountExists(request.accountId());
    Mockito.doNothing().when(budgetServiceClient).checkIfCategoryExists(request.categoryId());
    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscription(request))
        .thenReturn(subscription);
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);
    when(subscription.getStartDate()).thenReturn(LocalDate.now().plusMonths(3));
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscriptionDto(subscription))
        .thenReturn(subscriptionDto);

    SubscriptionDto result = subscriptionManagementService.createSubscription(request);

    assertNotNull(result);
    verify(subscriptionRepository, times(1)).save(subscription);
  }

  @Test
  void
      givenSubscriptionRequest_WhenCreateSubscription_ThenReturnSubscriptionAndCreatePreviousTransactions(
          @Mock Subscription subscription, @Mock SubscriptionDto subscriptionDto) {
    UUID userId = RandomUtils.randomUUID();
    LocalDate startDate = LocalDate.now().minusMonths(3);
    SubscriptionRequestDto request = createSubscriptionRequest(userId, startDate);
    Mockito.doNothing().when(accountServiceClient).checkIfAccountExists(request.accountId());
    Mockito.doNothing().when(budgetServiceClient).checkIfCategoryExists(request.categoryId());
    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscription(request))
        .thenReturn(subscription);
    when(subscription.getAmount()).thenReturn(RandomUtils.randomBigDecimal());
    when(subscription.getTotalAmount()).thenReturn(BigDecimal.ZERO);
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscriptionDto(subscription))
        .thenReturn(subscriptionDto);
    when(subscription.getRecurrenceRule()).thenReturn(request.recurrence().recurrenceRule());
    when(subscription.getStartDate()).thenReturn(request.recurrence().startDate());
    when(subscription.getEndDate()).thenReturn(request.recurrence().endDate());

    SubscriptionDto result = subscriptionManagementService.createSubscription(request);

    assertNotNull(result);
    verify(subscriptionRepository, times(1)).save(subscription);
  }
}
