package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetUpdateRequestDto(
    String budgetName,
    String description,
    BigDecimal amount,
    BigDecimal budgetLimit,
    String currency)
    implements Serializable {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
