package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import jakarta.validation.Valid;

public interface SubscriptionOperations {
  SubscriptionDto createSubscription(@Valid SubscriptionRequestDto request);
}
