package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryAnalyticsDistributionDto.Builder.class)
public class CategoryAnalyticsDistributionDto {
  private final PeriodDto period;
  private final UUID categoryId;
  private final AmountDto totalIncome;
  private final AmountDto totalExpense;
  private final AmountDto totalAmount;
  private final CategoryAnalyticsDistributionDto compare;

  public static class Builder {
    private PeriodDto period;
    private UUID categoryId;
    private AmountDto totalIncome;
    private AmountDto totalExpense;
    private AmountDto totalAmount;
    private CategoryAnalyticsDistributionDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withTotalIncome(AmountDto totalIncome) {
      this.totalIncome = totalIncome;
      return this;
    }

    public Builder withTotalExpense(AmountDto totalExpense) {
      this.totalExpense = totalExpense;
      return this;
    }

    public Builder withTotalAmount(AmountDto totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder withCompare(CategoryAnalyticsDistributionDto compare) {
      this.compare = compare;
      return this;
    }

    public CategoryAnalyticsDistributionDto build() {
      return new CategoryAnalyticsDistributionDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public CategoryAnalyticsDistributionDto(Builder builder) {
    this.period = builder.period;
    this.categoryId = builder.categoryId;
    this.totalIncome = builder.totalIncome;
    this.totalExpense = builder.totalExpense;
    this.totalAmount = builder.totalAmount;
    this.compare = builder.compare;
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public AmountDto getTotalIncome() {
    return totalIncome;
  }

  public AmountDto getTotalExpense() {
    return totalExpense;
  }

  public AmountDto getTotalAmount() {
    return totalAmount;
  }

  public CategoryAnalyticsDistributionDto getCompare() {
    return compare;
  }
}
