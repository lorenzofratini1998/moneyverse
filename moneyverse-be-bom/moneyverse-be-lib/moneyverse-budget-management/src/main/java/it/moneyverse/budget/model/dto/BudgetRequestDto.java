package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetRequestDto(
    @NotNull(message = "'Username' must not be null")
        @Size(max = 64, message = "'Username' must not exceed 64 characters")
        String username,
    @NotEmpty(message = "'Budget name' must not be empty or null") String budgetName,
    String description,
    BigDecimal budgetLimit,
    BigDecimal amount) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
