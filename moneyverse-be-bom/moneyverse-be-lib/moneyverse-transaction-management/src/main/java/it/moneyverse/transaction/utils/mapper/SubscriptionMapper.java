package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;

public class SubscriptionMapper {

  public static Subscription toSubscription(SubscriptionRequestDto request) {
    if (request == null) {
      return null;
    }
    Subscription subscription = new Subscription();
    subscription.setUserId(request.userId());
    subscription.setAccountId(request.accountId());
    subscription.setCategoryId(request.categoryId());
    subscription.setAmount(request.amount());
    subscription.setCurrency(request.currency());
    subscription.setSubscriptionName(request.subscriptionName());
    subscription.setRecurrenceRule(request.recurrence().recurrenceRule());
    subscription.setStartDate(request.recurrence().startDate());
    subscription.setEndDate(request.recurrence().endDate());
    return subscription;
  }

  public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
    if (subscription == null) {
      return null;
    }
    return SubscriptionDto.builder()
        .withSubscriptionId(subscription.getSubscriptionId())
        .withAccountId(subscription.getAccountId())
        .withUserId(subscription.getUserId())
        .withCategoryId(subscription.getCategoryId())
        .withAmount(subscription.getAmount())
        .withTotalAmount(subscription.getTotalAmount())
        .withCurrency(subscription.getCurrency())
        .withSubscriptionName(subscription.getSubscriptionName())
        .withRecurrenceRule(subscription.getRecurrenceRule())
        .withStartDate(subscription.getStartDate())
        .withEndDate(subscription.getEndDate())
        .withNextExecutionDate(subscription.getNextExecutionDate())
        .withActive(subscription.isActive())
        .withTransactions(TransactionMapper.toTransactionDto(subscription.getTransactions()))
        .build();
  }

  private SubscriptionMapper() {}
}
