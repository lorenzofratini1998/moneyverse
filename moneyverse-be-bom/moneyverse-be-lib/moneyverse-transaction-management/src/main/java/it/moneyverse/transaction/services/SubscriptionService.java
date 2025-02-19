package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;

public interface SubscriptionService {
  SubscriptionDto createSubscription(SubscriptionRequestDto request);
}
