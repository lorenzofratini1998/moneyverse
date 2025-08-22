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
import java.util.List;
import java.util.UUID;
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
    LOGGER.info("Adding transactions for subscription {}", subscription.getSubscriptionId());
    List<Transaction> transactions = createSubscriptionTransactions(subscription);
    transactions.forEach(subscription::addTransaction);
    updateTotalAmount(subscription);
  }

  private List<Transaction> createSubscriptionTransactions(Subscription subscription) {
    LOGGER.info(
        "Creating previous transactions for subscription {}", subscription.getSubscriptionId());
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
    if (request.categoryId() != null) {
      for (Transaction transaction : subscription.getTransactions()) {
        transaction.setCategoryId(request.categoryId());
        transaction.setBudgetId(
            budgetServiceClient.getBudgetId(request.categoryId(), transaction.getDate()));
      }
    }
    subscription = subscriptionRepository.save(subscription);
    if (request.categoryId() != null) {
      transactionEventPublisher.publish(subscription, originalSubscription, EventTypeEnum.UPDATE);
    }
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
