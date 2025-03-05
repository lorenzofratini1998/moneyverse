package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.transaction.model.SubscriptionTestFactory;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.SubscriptionMapper;
import java.time.LocalDate;
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
  @Mock private UserServiceClient userServiceClient;
  @Mock private TransactionFactoryService transactionFactoryService;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private TransactionEventPublisher transactionEventPublisher;
  @Mock private TransactionValidator transactionValidator;
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
    LocalDate startDate = LocalDate.now().plusMonths(1);
    SubscriptionRequestDto request =
        SubscriptionTestFactory.SubscriptionRequestBuilder.builder()
            .withRecurrence(startDate)
            .build();
    Mockito.doNothing().when(transactionValidator).validate(request);
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscription(request))
        .thenReturn(subscription);
    when(subscription.getRecurrenceRule()).thenReturn(request.recurrence().recurrenceRule());
    when(subscription.getStartDate()).thenReturn(request.recurrence().startDate());
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscriptionDto(subscription))
        .thenReturn(subscriptionDto);

    SubscriptionDto result = subscriptionManagementService.createSubscription(request);

    assertNotNull(result);
    verify(subscriptionRepository, times(1)).save(subscription);
    verify(transactionFactoryService, never())
        .createTransaction(any(Subscription.class), any(LocalDate.class));
  }

  @Test
  void
      givenSubscriptionRequest_WhenCreateSubscription_ThenReturnSubscriptionAndCreatePreviousTransactions(
          @Mock Subscription subscription,
          @Mock Transaction transaction,
          @Mock SubscriptionDto subscriptionDto) {
    LocalDate startDate = LocalDate.now().minusMonths(3);
    SubscriptionRequestDto request =
        SubscriptionTestFactory.SubscriptionRequestBuilder.builder()
            .withRecurrence(startDate)
            .build();
    Mockito.doNothing().when(transactionValidator).validate(request);
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscription(request))
        .thenReturn(subscription);
    when(subscription.getRecurrenceRule()).thenReturn(request.recurrence().recurrenceRule());
    when(subscription.getStartDate()).thenReturn(request.recurrence().startDate());

    when(transactionFactoryService.createTransaction(eq(subscription), any(LocalDate.class)))
        .thenReturn(transaction);
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);
    subscriptionMapper
        .when(() -> SubscriptionMapper.toSubscriptionDto(subscription))
        .thenReturn(subscriptionDto);

    SubscriptionDto result = subscriptionManagementService.createSubscription(request);

    assertNotNull(result);
    verify(subscriptionRepository, times(1)).save(subscription);
    verify(transactionFactoryService, times(4))
        .createTransaction(eq(subscription), any(LocalDate.class));
  }
}
