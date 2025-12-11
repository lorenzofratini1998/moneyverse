package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetRequestDto(
    @NotNull(message = "'Category ID' must not be null") UUID categoryId,
    @NotNull(message = "'Start date' must not be null") LocalDate startDate,
    @NotNull(message = "'End date' must not be null") LocalDate endDate,
    @NotNull(message = "'Budget limit' must not be null") BigDecimal budgetLimit,
    @NotEmpty(message = "'Currency' must not be null or empty") String currency) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
