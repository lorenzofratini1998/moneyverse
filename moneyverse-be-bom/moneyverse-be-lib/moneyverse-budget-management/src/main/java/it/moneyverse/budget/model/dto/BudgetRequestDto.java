package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetRequestDto(
    @NotNull(message = "'User ID' must not be null") UUID userId,
    @NotEmpty(message = "'Budget name' must not be empty or null") String budgetName,
    String description,
    BigDecimal budgetLimit,
    BigDecimal amount,
    @NotEmpty(message = "'Currency' must not be null or empty") String currency) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
