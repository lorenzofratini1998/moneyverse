package it.moneyverse.currency.model.repositories;

import it.moneyverse.currency.model.entities.ExchangeRate;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {

    @Query(value = "SELECT MIN(e.date) FROM ExchangeRate e")
    Optional<LocalDate> findMinDate();

  Optional<ExchangeRate> findExchangeRateByCurrencyFromAndCurrencyToAndDate(
      String currencyFrom, String currencyTo, LocalDate date);
}
