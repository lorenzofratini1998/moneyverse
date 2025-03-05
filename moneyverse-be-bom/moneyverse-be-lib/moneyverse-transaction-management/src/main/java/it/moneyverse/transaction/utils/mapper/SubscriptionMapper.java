package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.dto.SubscriptionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import java.util.Collections;
import java.util.List;

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

  public static List<SubscriptionDto> toSubscriptionDtoWithoutTransactions(
      List<Subscription> subscriptions) {
    if (subscriptions.isEmpty()) {
      return Collections.emptyList();
    }
    return subscriptions.stream()
        .map(SubscriptionMapper::toSubscriptionDtoWithoutTransactions)
        .toList();
  }

  public static SubscriptionDto toSubscriptionDtoWithoutTransactions(Subscription subscription) {
    if (subscription == null) {
      return null;
    }
    return toSubscriptionDtoBuilder(subscription).build();
  }

  public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
    if (subscription == null) {
      return null;
    }
    return toSubscriptionDtoBuilder(subscription)
        .withTransactions(TransactionMapper.toTransactionDto(subscription.getTransactions()))
        .build();
  }

  private static SubscriptionDto.Builder toSubscriptionDtoBuilder(Subscription subscription) {
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
        .withActive(subscription.isActive());
  }

  public static Subscription partialUpdate(
      Subscription subscription, SubscriptionUpdateRequestDto request) {
    if (request == null) {
      return subscription;
    }

    if (request.accountId() != null) {
      subscription.setAccountId(request.accountId());
    }
    if (request.categoryId() != null) {
      subscription.setCategoryId(request.categoryId());
    }
    if (request.subscriptionName() != null) {
      subscription.setSubscriptionName(request.subscriptionName());
    }
    if (request.amount() != null) {
      subscription.setAmount(request.amount());
    }
    if (request.totalAmount() != null) {
      subscription.setTotalAmount(request.totalAmount());
    }
    if (request.currency() != null) {
      subscription.setCurrency(request.currency());
    }
    if (request.recurrenceRule() != null) {
      subscription.setRecurrenceRule(request.recurrenceRule());
    }
    if (request.startDate() != null) {
      subscription.setStartDate(request.startDate());
    }
    if (request.endDate() != null) {
      subscription.setEndDate(request.endDate());
    }
    if (request.nextExecutionDate() != null) {
      subscription.setNextExecutionDate(request.nextExecutionDate());
    }
    if (request.isActive() != null) {
      subscription.setActive(request.isActive());
    }
    return subscription;
  }

  private SubscriptionMapper() {}
}
