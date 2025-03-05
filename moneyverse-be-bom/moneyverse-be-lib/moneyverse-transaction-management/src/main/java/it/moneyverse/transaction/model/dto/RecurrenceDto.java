package it.moneyverse.transaction.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import net.fortuna.ical4j.model.property.RRule;

public record RecurrenceDto(
    @NotEmpty(message = "Recurrence rule is required") String recurrenceRule,
    @NotNull(message = "Start date is required") LocalDate startDate,
    LocalDate endDate) {

  @AssertTrue(message = "Recurrence rule is not valid")
  public boolean isRecurrenceRuleValid() {
    try {
      new RRule<>(recurrenceRule);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @AssertTrue(message = "End date must be after start date")
  public boolean isEndDateAfterStartDate() {
    return endDate == null || endDate.isAfter(startDate);
  }
}
