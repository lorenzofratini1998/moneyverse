package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.transaction.enums.SubscriptionSseEventEnum;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.dto.SubscriptionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.DateCalculator;
import it.moneyverse.transaction.utils.mapper.SubscriptionMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionManagementService implements SubscriptionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionManagementService.class);
  private final AccountServiceClient accountServiceClient;
  private final UserServiceClient userServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final TransactionFactoryService transactionFactoryService;
  private final TransactionEventPublisher transactionEventPublisher;
  private final SubscriptionRepository subscriptionRepository;
  private final TransactionValidator transactionValidator;
  private final SecurityService securityService;
  private final SseEventService eventService;

  public SubscriptionManagementService(
      AccountServiceClient accountServiceClient,
      UserServiceClient userServiceClient,
      BudgetServiceClient budgetServiceClient,
      TransactionFactoryService transactionFactoryService,
      TransactionEventPublisher transactionEventPublisher,
      SubscriptionRepository subscriptionRepository,
      TransactionValidator transactionValidator,
      SecurityService securityService,
      SseEventService eventService) {
    this.accountServiceClient = accountServiceClient;
    this.userServiceClient = userServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.transactionFactoryService = transactionFactoryService;
    this.transactionEventPublisher = transactionEventPublisher;
    this.subscriptionRepository = subscriptionRepository;
    this.transactionValidator = transactionValidator;
    this.securityService = securityService;
    this.eventService = eventService;
  }

  @Override
  @Transactional
  public SubscriptionDto createSubscription(SubscriptionRequestDto request) {
    transactionValidator.validate(request);
    LOGGER.info("Creating subscription for user {}", request.userId());
    Subscription subscription = SubscriptionMapper.toSubscription(request);
    subscription.setNextExecutionDate(calculateNextExecutionDate(subscription));
    if (subscription.getNextExecutionDate() == null) {
      subscription.setActive(false);
    }
    if (subscriptionHasStarted(request)) {
      addTransactions(subscription);
    }
    subscription = subscriptionRepository.save(subscription);
    transactionEventPublisher.publish(subscription, EventTypeEnum.CREATE);
    SubscriptionDto result = SubscriptionMapper.toSubscriptionDto(subscription);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        SubscriptionSseEventEnum.SUBSCRIPTION_CREATED.name(),
        result.getSubscriptionId());
    return result;
  }

  private boolean subscriptionHasStarted(SubscriptionRequestDto request) {
    return !request.recurrence().startDate().isAfter(LocalDate.now());
  }

  private void addTransactions(Subscription subscription) {
    LOGGER.info("Adding transactions for subscription {} with startDate {}", subscription.getSubscriptionName(), subscription.getStartDate());
    List<Transaction> transactions = createSubscriptionTransactions(subscription);
    transactions.forEach(subscription::addTransaction);
    updateTotalAmount(subscription);
  }

  private List<Transaction> createSubscriptionTransactions(Subscription subscription) {
    LOGGER.info(
        "Creating previous transactions for subscription {}", subscription.getSubscriptionName());
    DateCalculator dateCalculator = new DateCalculator(subscription.getRecurrenceRule());
    List<LocalDate> dates =
        dateCalculator.calculateDates(subscription.getStartDate(), getEndDate(subscription));
    return dates.stream()
        .map(date -> transactionFactoryService.createTransaction(subscription, date))
        .toList();
  }

  private LocalDate getEndDate(Subscription subscription) {
    return subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDate.now())
        ? subscription.getEndDate()
        : LocalDate.now();
  }

  private void updateTotalAmount(Subscription subscription) {
    subscription.setTotalAmount(
        subscription.getTransactions().stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
  }

  @Override
  @Transactional(readOnly = true)
  public SubscriptionDto getSubscription(UUID subscriptionId) {
    return SubscriptionMapper.toSubscriptionDto(getSubscriptionById(subscriptionId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<SubscriptionDto> getSubscriptionsByUserId(UUID userId) {
    List<Subscription> userSubscriptions = subscriptionRepository.findSubscriptionByUserId(userId);
    return SubscriptionMapper.toSubscriptionDtoWithoutTransactions(userSubscriptions);
  }

  @Override
  @Transactional
  public void deleteSubscription(UUID subscriptionId) {
    Subscription subscription = getSubscriptionById(subscriptionId);
    LOGGER.info("Deleting subscription {}", subscriptionId);
    subscriptionRepository.delete(subscription);
    transactionEventPublisher.publish(subscription, EventTypeEnum.DELETE);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        SubscriptionSseEventEnum.SUBSCRIPTION_DELETED.name(),
        subscription.getSubscriptionId());
  }

  @Override
  @Transactional
  public SubscriptionDto updateSubscription(
      UUID subscriptionId, SubscriptionUpdateRequestDto request) {
    Subscription subscription = getSubscriptionById(subscriptionId);
    Subscription originalSubscription = subscription.copy();

    transactionValidator.validate(request);
    LOGGER.info("Updating subscription {}", subscriptionId);

    subscription = SubscriptionMapper.partialUpdate(subscription, request);
    syncSubscriptionTransactions(subscription);
    if (subscription.getTransactions().size() != originalSubscription.getTransactions().size()) {
        calculateNextExecutionDate(subscription);
    }
    updateTransactionsCategory(subscription, request.categoryId());

    subscription = subscriptionRepository.save(subscription);

    transactionEventPublisher.publish(subscription, originalSubscription, EventTypeEnum.UPDATE);
    SubscriptionDto result = SubscriptionMapper.toSubscriptionDto(subscription);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        SubscriptionSseEventEnum.SUBSCRIPTION_UPDATED.name(),
        result.getSubscriptionId());
    return result;
  }

  private Subscription getSubscriptionById(UUID subscriptionId) {
    return subscriptionRepository
        .findById(subscriptionId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Subscription %s not found".formatted(subscriptionId)));
  }

  private void updateTransactionsCategory(Subscription subscription, UUID categoryId) {
    if (categoryId == null) {
      return;
    }

    subscription
        .getTransactions()
        .forEach(
            transaction -> {
              transaction.setCategoryId(categoryId);
              transaction.setBudgetId(
                  budgetServiceClient.getBudgetId(categoryId, transaction.getDate()));
            });
  }

  private void syncSubscriptionTransactions(Subscription subscription) {
    DateCalculator dateCalculator = new DateCalculator(subscription.getRecurrenceRule());
    List<LocalDate> expectedDates =
        dateCalculator.calculateDates(subscription.getStartDate(), getEndDate(subscription));

    Map<LocalDate, Transaction> existingByDate =
        subscription.getTransactions().stream()
            .collect(Collectors.toMap(Transaction::getDate, Function.identity()));

    createMissingTransactions(subscription, expectedDates, existingByDate);
    removeObsoleteTransactions(subscription, expectedDates);
    updateExistingTransactions(subscription, expectedDates, existingByDate);

    updateTotalAmount(subscription);
  }

  private void createMissingTransactions(
      Subscription subscription,
      List<LocalDate> expectedDates,
      Map<LocalDate, Transaction> existingByDate) {

    LOGGER.info(
        "Creating missing transactions for subscription {}", subscription.getSubscriptionId());
    expectedDates.stream()
        .filter(date -> !existingByDate.containsKey(date))
        .forEach(
            date -> {
              Transaction transaction =
                  transactionFactoryService.createTransaction(subscription, date);
              subscription.addTransaction(transaction);
            });
  }

  private void removeObsoleteTransactions(
      Subscription subscription, List<LocalDate> expectedDates) {
    LOGGER.info(
        "Removing obsolete transactions for subscription {}", subscription.getSubscriptionId());
    Set<LocalDate> expectedDatesSet = new HashSet<>(expectedDates);
    subscription.getTransactions().removeIf(t -> !expectedDatesSet.contains(t.getDate()));
  }

  private void updateExistingTransactions(
      Subscription subscription,
      List<LocalDate> expectedDates,
      Map<LocalDate, Transaction> existingByDate) {

    LOGGER.info(
        "Updating existing transactions for subscription {}", subscription.getSubscriptionId());
    expectedDates.stream()
        .map(existingByDate::get)
        .filter(Objects::nonNull)
        .forEach(
            transaction -> {
              Transaction managedTransaction =
                  subscription.getTransaction(transaction.getTransactionId());
              transactionFactoryService.updateTransaction(
                  managedTransaction, subscription, transaction.getDate());
            });
  }

  @Override
  @Transactional
  public void deleteSubscriptionsByUserId(UUID userId) {
    LOGGER.info("Deleting subscriptions by user id {}", userId);
    userServiceClient.checkIfUserStillExist(userId);
    List<Subscription> subscriptions = subscriptionRepository.findSubscriptionByUserId(userId);
    subscriptionRepository.deleteAll(subscriptions);
  }

  @Override
  @Transactional
  public void deleteSubscriptionsByAccountId(UUID accountId) {
    LOGGER.info("Deleting subscriptions by account id {}", accountId);
    accountServiceClient.checkIfAccountStillExists(accountId);
    List<Subscription> subscriptions =
        subscriptionRepository.findSubscriptionByAccountId(accountId);
    subscriptionRepository.deleteAll(subscriptions);
    subscriptions.forEach(
        subscription -> transactionEventPublisher.publish(subscription, EventTypeEnum.DELETE));
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        SubscriptionSseEventEnum.SUBSCRIPTION_DELETED.name(),
        accountId);
  }

  @Override
  public LocalDate calculateNextExecutionDate(Subscription subscription) {
    DateCalculator dateCalculator = new DateCalculator(subscription.getRecurrenceRule());
    return subscription.getEndDate() != null
        ? dateCalculator.getNextOccurrence(subscription.getStartDate(), subscription.getEndDate())
        : dateCalculator.getNextOccurrence(subscription.getStartDate());
  }
}
