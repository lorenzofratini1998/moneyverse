package it.moneyverse.currency.model.repositories;

import it.moneyverse.currency.model.entities.Currency;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {}
