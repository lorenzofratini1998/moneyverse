package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.model.dto.CategoryDto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = BudgetDto.Builder.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "budgetId")
public class BudgetDto implements Serializable {

  private final UUID budgetId;
  private final CategoryDto category;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final BigDecimal amount;
  private final BigDecimal budgetLimit;
  private final String currency;

  public BudgetDto(Builder builder) {
    this.budgetId = builder.budgetId;
    this.category = builder.category;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.amount = builder.amount;
    this.budgetLimit = builder.budgetLimit;
    this.currency = builder.currency;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public CategoryDto getCategory() {
    return category;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getBudgetLimit() {
    return budgetLimit;
  }

  public String getCurrency() {
    return currency;
  }

  public static class Builder {
    private UUID budgetId;
    private CategoryDto category;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private BigDecimal budgetLimit;
    private String currency;

    public Builder withBudgetId(UUID budgetId) {
      this.budgetId = budgetId;
      return this;
    }

    public Builder withCategory(CategoryDto category) {
      this.category = category;
      return this;
    }

    public Builder withStartDate(LocalDate startDate) {
      this.startDate = startDate;
      return this;
    }

    public Builder withEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withBudgetLimit(BigDecimal budgetLimit) {
      this.budgetLimit = budgetLimit;
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
}
