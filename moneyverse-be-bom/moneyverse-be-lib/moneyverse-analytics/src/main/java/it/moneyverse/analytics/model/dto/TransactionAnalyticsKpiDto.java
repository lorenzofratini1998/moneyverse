package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransactionAnalyticsKpiDto.Builder.class)
public class TransactionAnalyticsKpiDto {
  private final PeriodDto period;
  private final CountDto numberOfTransactions;
  private final AmountDto totalIncome;
  private final AmountDto totalExpense;
  private final AmountDto averageAmount;
  private final AmountDto quantile90;
  private final TransactionAnalyticsKpiDto compare;

  public static class Builder {
    private PeriodDto period;
    private CountDto numberOfTransactions;
    private AmountDto totalIncome;
    private AmountDto totalExpense;
    private AmountDto averageAmount;
    private AmountDto quantile90;
    private TransactionAnalyticsKpiDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withNumberOfTransactions(CountDto numberOfTransactions) {
      this.numberOfTransactions = numberOfTransactions;
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

    public Builder withAverageAmount(AmountDto averageAmount) {
      this.averageAmount = averageAmount;
      return this;
    }

    public Builder withQuantile90(AmountDto quantile90) {
      this.quantile90 = quantile90;
      return this;
    }

    public Builder withCompare(TransactionAnalyticsKpiDto compare) {
      this.compare = compare;
      return this;
    }

    public TransactionAnalyticsKpiDto build() {
      return new TransactionAnalyticsKpiDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private TransactionAnalyticsKpiDto(Builder builder) {
    this.period = builder.period;
    this.numberOfTransactions = builder.numberOfTransactions;
    this.totalIncome = builder.totalIncome;
    this.totalExpense = builder.totalExpense;
    this.averageAmount = builder.averageAmount;
    this.quantile90 = builder.quantile90;
    this.compare = builder.compare;
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public CountDto getNumberOfTransactions() {
    return numberOfTransactions;
  }

  public AmountDto getTotalIncome() {
    return totalIncome;
  }

  public AmountDto getTotalExpense() {
    return totalExpense;
  }

  public AmountDto getAverageAmount() {
    return averageAmount;
  }

  public AmountDto getQuantile90() {
    return quantile90;
  }

  public TransactionAnalyticsKpiDto getCompare() {
    return compare;
  }
}
