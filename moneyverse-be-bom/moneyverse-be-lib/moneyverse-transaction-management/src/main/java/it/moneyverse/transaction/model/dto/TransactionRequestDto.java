package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TransactionRequestDto(
    @NotNull(message = "'Username' must not be null")
        @Size(max = 64, message = "'Username' must not exceed 64 characters")
        String username,
    @NotNull(message = "'Account ID' must not be null") UUID accountId,
    UUID budgetId,
    @NotNull(message = "'Date' must not be null") LocalDate date,
    String description,
    @NotNull(message = "'Amount' must not be null") BigDecimal amount,
    @NotEmpty(message = "'Currency' must not be null or empty") String currency,
    Set<UUID> tags) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
