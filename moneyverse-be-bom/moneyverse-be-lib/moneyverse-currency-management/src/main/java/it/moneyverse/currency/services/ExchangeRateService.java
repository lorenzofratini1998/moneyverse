package it.moneyverse.currency.services;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;

public interface ExchangeRateService {

    ResponseEntity<String> readExchangeRates(LocalDate startPeriod, LocalDate endPeriod);
    void initializeExchangeRates();

}
