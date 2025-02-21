package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;

public interface SubscriptionOperations {
  SubscriptionDto createSubscription(@Valid SubscriptionRequestDto request);

  SubscriptionDto getSubscription(UUID subscriptionId);
}
