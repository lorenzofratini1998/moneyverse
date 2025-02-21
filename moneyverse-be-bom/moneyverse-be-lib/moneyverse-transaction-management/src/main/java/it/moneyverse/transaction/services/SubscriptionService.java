package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.SubscriptionDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import java.util.UUID;

public interface SubscriptionService {
  SubscriptionDto createSubscription(SubscriptionRequestDto request);

  SubscriptionDto getSubscription(UUID subscriptionId);
}
