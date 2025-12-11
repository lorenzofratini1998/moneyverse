package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.dto.SubscriptionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
  SubscriptionDto createSubscription(SubscriptionRequestDto request);

  SubscriptionDto getSubscription(UUID subscriptionId);

  List<SubscriptionDto> getSubscriptionsByUserId(UUID userId);

  void deleteSubscription(UUID subscriptionId);

  SubscriptionDto updateSubscription(UUID subscriptionId, SubscriptionUpdateRequestDto request);

  void deleteSubscriptionsByUserId(UUID userId);

  void deleteSubscriptionsByAccountId(UUID accountId);

  LocalDate calculateNextExecutionDate(Subscription subscription);
}
