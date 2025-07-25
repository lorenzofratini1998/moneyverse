package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransactionAnalyticsTrendDto.Builder.class)
public class TransactionAnalyticsTrendDto implements Serializable {
  private final PeriodDto period;
  private final List<ExpenseIncomeDto> data;
  private final TransactionAnalyticsTrendDto compare;

  public TransactionAnalyticsTrendDto(Builder builder) {
    this.period = builder.period;
    this.data = builder.data;
    this.compare = builder.compare;
  }

  public static class Builder {
    private PeriodDto period;
    private List<ExpenseIncomeDto> data;
    private TransactionAnalyticsTrendDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withData(List<ExpenseIncomeDto> data) {
      this.data = data;
      return this;
    }

    public Builder withCompare(TransactionAnalyticsTrendDto compare) {
      this.compare = compare;
      return this;
    }

    public TransactionAnalyticsTrendDto build() {
      return new TransactionAnalyticsTrendDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public List<ExpenseIncomeDto> getData() {
    return data;
  }

  public TransactionAnalyticsTrendDto getCompare() {
    return compare;
  }
}
