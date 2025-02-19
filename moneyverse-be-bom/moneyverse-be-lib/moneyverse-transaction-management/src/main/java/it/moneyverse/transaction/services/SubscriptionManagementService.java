package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.utils.mapper.SubscriptionMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionManagementService implements SubscriptionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionManagementService.class);
  private final AccountServiceClient accountServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final ApplicationEventPublisher eventPublisher;
  private final SubscriptionRepository subscriptionRepository;

  public SubscriptionManagementService(
      AccountServiceClient accountServiceClient,
      CurrencyServiceClient currencyServiceClient,
      BudgetServiceClient budgetServiceClient,
      ApplicationEventPublisher eventPublisher,
      SubscriptionRepository subscriptionRepository) {
    this.accountServiceClient = accountServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.eventPublisher = eventPublisher;
    this.subscriptionRepository = subscriptionRepository;
  }

  @Override
  @Transactional
  public SubscriptionDto createSubscription(SubscriptionRequestDto request) {
    accountServiceClient.checkIfAccountExists(request.accountId());
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
    }
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    LOGGER.info("Creating subscription for user {}", request.userId());
    Subscription subscription = SubscriptionMapper.toSubscription(request);
    if (request.recurrence().startDate().isBefore(LocalDate.now())
        || request.recurrence().startDate().isEqual(LocalDate.now())) {
      addSubscriptionTransactions(subscription);
    }
    subscription.setNextExecutionDate(calculateNextExecutionDate(subscription));
    subscription = subscriptionRepository.save(subscription);
    publishEvent(subscription, EventTypeEnum.CREATE);
    return SubscriptionMapper.toSubscriptionDto(subscription);
  }

  private void addSubscriptionTransactions(Subscription subscription) {
    for (Transaction transaction : createSubscriptionTransactions(subscription)) {
      subscription.addTransaction(transaction);
    }
  }

  private List<Transaction> createSubscriptionTransactions(Subscription subscription) {
    LOGGER.info(
        "Creating previous transactions for subscription {}", subscription.getSubscriptionId());
    List<Transaction> transactions = new ArrayList<>();
    RRule<LocalDate> rrule = new RRule<>(subscription.getRecurrenceRule());
    Recur<LocalDate> recur = rrule.getRecur();
    LocalDate startDate = subscription.getStartDate();
    LocalDate endDate =
        subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDate.now())
            ? subscription.getEndDate()
            : LocalDate.now();
    List<LocalDate> dates = recur.getDates(startDate, endDate);
    for (LocalDate date : dates) {
      transactions.add(createTransaction(subscription, date));
    }
    return transactions;
  }

  private Transaction createTransaction(Subscription subscription, LocalDate date) {
    Transaction transaction = new Transaction();
    transaction.setUserId(subscription.getUserId());
    transaction.setAccountId(subscription.getAccountId());
    transaction.setCategoryId(subscription.getCategoryId());
    transaction.setAmount(subscription.getAmount());
    transaction.setCurrency(subscription.getCurrency());
    transaction.setDescription(subscription.getSubscriptionName());
    transaction.setDate(date);
    return transaction;
  }

  private LocalDate calculateNextExecutionDate(Subscription subscription) {
    LocalDate today = LocalDate.now();
    if (subscription.getStartDate().isAfter(today)) {
      return subscription.getStartDate();
    }
    RRule<LocalDate> rrule = new RRule<>(subscription.getRecurrenceRule());
    Recur<LocalDate> recur = rrule.getRecur();
    LocalDate startInterval = subscription.getStartDate();
    ;
    LocalDate endInterval = today.plusYears(1);
    if (subscription.getEndDate() != null) {
      endInterval =
          subscription.getEndDate().isBefore(endInterval) ? subscription.getEndDate() : endInterval;
    }
    List<LocalDate> dates =
        recur.getDates(startInterval, endInterval).stream()
            .filter(date -> date.isAfter(today))
            .toList();
    return dates.isEmpty() ? null : dates.getFirst();
  }

  private void publishEvent(Subscription subscription, EventTypeEnum eventType) {
    for (Transaction transaction : subscription.getTransactions()) {
      TransactionEvent event = new TransactionEvent();
      event.setTransactionId(transaction.getTransactionId());
      event.setAccountId(subscription.getAccountId());
      event.setCategoryId(subscription.getCategoryId());
      event.setDate(transaction.getDate());
      event.setAmount(transaction.getAmount());
      event.setEventType(eventType);
      eventPublisher.publishEvent(event);
    }
  }
}
