package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.dto.SubscriptionUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface SubscriptionOperations {
  SubscriptionDto createSubscription(@Valid SubscriptionRequestDto request);

  SubscriptionDto getSubscription(UUID subscriptionId);

  List<SubscriptionDto> getSubscriptions(UUID userId);

  void deleteSubscription(UUID subscriptionId);

  SubscriptionDto updateSubscription(UUID subscriptionId, SubscriptionUpdateRequestDto request);
}
