package it.moneyverse.analytics.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

public record PeriodDto(
    @NotNull(message = "Start date is required") LocalDate startDate,
    @NotNull(message = "End date is required") LocalDate endDate)
    implements Serializable {

  @AssertTrue(message = "End date must be after start date")
  public boolean isEndDateAfterStartDate() {
    if (startDate != null && endDate != null) {
      return endDate.isAfter(startDate);
    }
    return true;
  }
}
