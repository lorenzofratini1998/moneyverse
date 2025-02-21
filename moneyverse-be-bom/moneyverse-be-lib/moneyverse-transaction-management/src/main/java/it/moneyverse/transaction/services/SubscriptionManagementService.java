package it.moneyverse.transaction.services;

import static it.moneyverse.transaction.utils.SubscriptionUtils.calculateNextExecutionDate;
import static it.moneyverse.transaction.utils.SubscriptionUtils.createSubscriptionTransaction;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.SubscriptionRepository;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.SubscriptionMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionManagementService implements SubscriptionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionManagementService.class);
  private final AccountServiceClient accountServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final TransactionEventPublisher transactionEventPublisher;
  private final SubscriptionRepository subscriptionRepository;

  public SubscriptionManagementService(
      AccountServiceClient accountServiceClient,
      CurrencyServiceClient currencyServiceClient,
      BudgetServiceClient budgetServiceClient,
      TransactionEventPublisher transactionEventPublisher,
      SubscriptionRepository subscriptionRepository) {
    this.accountServiceClient = accountServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.transactionEventPublisher = transactionEventPublisher;
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
    transactionEventPublisher.publishEvent(subscription, EventTypeEnum.CREATE);
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
      Transaction transaction = createSubscriptionTransaction(subscription, date);
      subscription.setTotalAmount(subscription.getTotalAmount().add(transaction.getAmount()));
      transactions.add(transaction);
    }
    return transactions;
  }

  @Override
  public SubscriptionDto getSubscription(UUID subscriptionId) {
    return SubscriptionMapper.toSubscriptionDto(getSubscriptionById(subscriptionId));
  }

  @Override
  public List<SubscriptionDto> getSubscriptionsByUserId(UUID userId) {
    List<Subscription> userSubscriptions = subscriptionRepository.findSubscriptionByUserId(userId);
    return SubscriptionMapper.toSubscriptionDtoWithoutTransactions(userSubscriptions);
  }

  @Override
  public void deleteSubscription(UUID subscriptionId) {
    Subscription subscription = getSubscriptionById(subscriptionId);
    LOGGER.info("Deleting subscription {}", subscriptionId);
    subscriptionRepository.delete(subscription);
    transactionEventPublisher.publishEvent(subscription, EventTypeEnum.DELETE);
  }

  private Subscription getSubscriptionById(UUID subscriptionId) {
    return subscriptionRepository
        .findById(subscriptionId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Subscription %s not found".formatted(subscriptionId)));
  }
}
