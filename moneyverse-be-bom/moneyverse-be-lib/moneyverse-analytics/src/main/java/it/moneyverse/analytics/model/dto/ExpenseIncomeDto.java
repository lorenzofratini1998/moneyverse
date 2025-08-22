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
  private final AmountDto total;

  public static class Builder {
    private PeriodDto period;
    private AmountDto expense;
    private AmountDto income;
    private AmountDto total;

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

    public Builder withTotal(AmountDto total) {
      this.total = total;
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
    this.total = builder.total;
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

  public AmountDto getTotal() {
    return total;
  }
}
