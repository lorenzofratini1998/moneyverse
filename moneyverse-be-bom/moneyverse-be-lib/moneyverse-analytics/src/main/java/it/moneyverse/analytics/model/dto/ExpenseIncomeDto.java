package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ExpenseIncomeDto.Builder.class)
public class ExpenseIncomeDto implements Serializable {
  private final PeriodDto period;
  private final AmountDto expense;
  private final AmountDto income;

  public static class Builder {
    private PeriodDto period;
    private AmountDto expense;
    private AmountDto income;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withExpense(AmountDto expense) {
      this.expense = expense;
      return this;
    }

    public Builder withIncome(AmountDto income) {
      this.income = income;
      return this;
    }

    public ExpenseIncomeDto build() {
      return new ExpenseIncomeDto(this);
    }
  }

  private ExpenseIncomeDto(Builder builder) {
    this.period = builder.period;
    this.expense = builder.expense;
    this.income = builder.income;
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public AmountDto getExpense() {
    return expense;
  }

  public AmountDto getIncome() {
    return income;
  }
}
