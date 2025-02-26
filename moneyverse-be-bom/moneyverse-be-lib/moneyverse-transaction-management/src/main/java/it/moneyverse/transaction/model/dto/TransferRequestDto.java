package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferRequestDto(
    @NotNull(message = "User ID must not be null") UUID userId,
    @NotNull(message = "Source account ID must not be null") UUID fromAccount,
    @NotNull(message = "Destination account ID must not be null") UUID toAccount,
    @NotNull(message = "Amount must not be null") BigDecimal amount,
    @NotNull(message = "Date must not be null") LocalDate date,
    @NotEmpty(message = "Currency must not be null or empty") String currency) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
