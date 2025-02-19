package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Subscription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {}
