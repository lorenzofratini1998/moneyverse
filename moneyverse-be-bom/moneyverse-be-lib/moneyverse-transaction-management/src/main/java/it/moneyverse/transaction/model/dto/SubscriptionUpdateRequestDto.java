package it.moneyverse.transaction.model.dto;

import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import net.fortuna.ical4j.model.property.RRule;

public record SubscriptionUpdateRequestDto(
    UUID accountId,
    UUID categoryId,
    String subscriptionName,
    BigDecimal amount,
    BigDecimal totalAmount,
    String currency,
    RecurrenceDto recurrence,
    LocalDate nextExecutionDate,
    Boolean isActive) {
  @AssertTrue(message = "Recurrence rule is not valid")
  public boolean isRecurrenceRuleValid() {
    try {
      if (recurrence == null || recurrence.recurrenceRule() == null) {
        return true;
      }
      new RRule<>(recurrence.recurrenceRule());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
