package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.transaction.enums.PeriodDashboardEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDate;

public record PeriodDashboardDto(
    @NotEmpty(message = "Period is required") PeriodDashboardEnum period,
    Integer month,
    Integer year,
    LocalDate startDate,
    LocalDate endDate)
    implements Serializable {
  @AssertTrue(message = "Month and year are required with period MONTHLY")
  public boolean isMonthAndYearRequired() {
    if (period == PeriodDashboardEnum.MONTHLY) {
      return month != null && year != null;
    }
    return true;
  }

  @AssertTrue(message = "Year is required with period YEARLY")
  public boolean isYearRequired() {
    if (period == PeriodDashboardEnum.YEARLY) {
      return year != null;
    }
    return true;
  }

  @AssertTrue(message = "Start date and end date are required with period CUSTOM")
  public boolean isStartDateAndEndDateRequired() {
    if (period == PeriodDashboardEnum.CUSTOM) {
      return startDate != null && endDate != null;
    }
    return true;
  }

  @AssertTrue(message = "End date must be after start date")
  public boolean isEndDateAfterStartDate() {
    if (startDate != null && endDate != null) {
      return endDate.isAfter(startDate);
    }
    return true;
  }

  @AssertTrue(message = "Month and year must be valid")
  public boolean isMonthAndYearValid() {
    if (month != null && year != null) {
      return month >= 1 && month <= 12;
    }
    return true;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
