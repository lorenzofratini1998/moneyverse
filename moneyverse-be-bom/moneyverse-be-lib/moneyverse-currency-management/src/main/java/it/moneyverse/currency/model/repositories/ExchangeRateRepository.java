package it.moneyverse.currency.model.repositories;

import it.moneyverse.currency.model.entities.ExchangeRate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {}
