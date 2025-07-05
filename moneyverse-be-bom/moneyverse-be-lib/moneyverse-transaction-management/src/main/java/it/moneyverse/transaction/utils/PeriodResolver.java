package it.moneyverse.transaction.utils;

import it.moneyverse.transaction.model.dto.PeriodDashboardDto;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class PeriodResolver {
  public PeriodDashboardDto resolvePeriod(PeriodDashboardDto request) {
    LocalDate startDate =
        switch (request.period()) {
          case MONTHLY -> YearMonth.of(request.year(), request.month()).atDay(1);
          case YEARLY -> LocalDate.of(request.year(), 1, 1);
          default -> request.startDate();
        };

    LocalDate endDate =
        switch (request.period()) {
          case MONTHLY -> YearMonth.of(request.year(), request.month()).atEndOfMonth();
          case YEARLY -> LocalDate.of(request.year(), 12, 31);
          default -> request.endDate();
        };

    return new PeriodDashboardDto(null, null, null, startDate, endDate);
  }
}
