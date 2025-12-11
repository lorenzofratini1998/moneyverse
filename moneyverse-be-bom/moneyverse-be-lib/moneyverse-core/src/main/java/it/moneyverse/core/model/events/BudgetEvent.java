package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = BudgetEvent.Builder.class)
public class BudgetEvent extends AbstractEvent {

  private final UUID budgetId;
  private final UUID categoryId;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final BigDecimal amount;
  private final BigDecimal budgetLimit;
  private final String currency;

  public BudgetEvent(Builder builder) {
    super(builder);
    this.budgetId = builder.budgetId;
    this.categoryId = builder.categoryId;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.amount = builder.amount;
    this.budgetLimit = builder.budgetLimit;
    this.currency = builder.currency;
  }

  public static class Builder extends AbstractBuilder<BudgetEvent, Builder> {
    private UUID budgetId;
    private UUID categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private BigDecimal budgetLimit;
    private String currency;

    public Builder withBudgetId(UUID budgetId) {
      this.budgetId = budgetId;
      return this;
    }

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
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

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public BudgetEvent build() {
      return new BudgetEvent(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public UUID key() {
    return null;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public UUID getCategoryId() {
    return categoryId;
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
}
