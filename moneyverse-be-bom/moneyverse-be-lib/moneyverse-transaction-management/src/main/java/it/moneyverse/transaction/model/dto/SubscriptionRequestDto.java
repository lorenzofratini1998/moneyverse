package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionRequestDto(
    @NotNull(message = "'User ID' must not be null") UUID userId,
    @NotNull(message = "'Account ID' must not be null") UUID accountId,
    UUID categoryId,
    @NotEmpty(message = "'Subscription name' must not be null or empty") String subscriptionName,
    @NotNull(message = "'Amount' must not be null") BigDecimal amount,
    @NotEmpty(message = "'Currency' must not be null or empty") String currency,
    @Valid @NotNull(message = "'Recurrence' must not be null") RecurrenceDto recurrence) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
