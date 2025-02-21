package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
  List<Subscription> findSubscriptionByNextExecutionDateAndIsActive(
      LocalDate nextExecutionDate, Boolean isActive);

  boolean existsBySubscriptionIdAndUserId(UUID subscriptionId, UUID userId);
}
