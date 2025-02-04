package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = BudgetDto.Builder.class)
public class BudgetDto implements Serializable {

  private final UUID budgetId;
  private final UUID userId;
  private final String budgetName;
  private final String description;
  private final BigDecimal budgetLimit;
  private final BigDecimal amount;
  private final String currency;

  public BudgetDto(Builder builder) {
    this.budgetId = builder.budgetId;
    this.userId = builder.userId;
    this.budgetName = builder.budgetName;
    this.description = builder.description;
    this.budgetLimit = builder.budgetLimit;
    this.amount = builder.amount;
    this.currency = builder.currency;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getBudgetName() {
    return budgetName;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getBudgetLimit() {
    return budgetLimit;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public static class Builder {
    private UUID budgetId;
    private UUID userId;
    private String budgetName;
    private String description;
    private BigDecimal budgetLimit;
    private BigDecimal amount;
    private String currency;

    public Builder withBudgetId(UUID budgetId) {
      this.budgetId = budgetId;
      return this;
    }

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withBudgetName(String budgetName) {
      this.budgetName = budgetName;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withBudgetLimit(BigDecimal budgetLimit) {
      this.budgetLimit = budgetLimit;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public BudgetDto build() {
      return new BudgetDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
