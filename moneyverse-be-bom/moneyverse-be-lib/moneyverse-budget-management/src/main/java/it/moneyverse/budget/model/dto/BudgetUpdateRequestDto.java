package it.moneyverse.budget.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetUpdateRequestDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal amount,
    BigDecimal budgetLimit,
    String currency)
    implements Serializable {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
