package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TransactionUpdateRequestDto(
    UUID accountId,
    UUID budgetId,
    LocalDate date,
    String description,
    BigDecimal amount,
    String currency,
    Set<UUID> tags)
    implements Serializable {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
