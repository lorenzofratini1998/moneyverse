package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.YearMonth;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TrendDto.Builder.class)
public class TrendDto {
  private final YearMonth period;
  private final BigDecimal amount;
  private final BigDecimal percentage;

  public TrendDto(Builder builder) {
    this.period = builder.period;
    this.amount = builder.amount;
    this.percentage = builder.percentage;
  }

  public static class Builder {
    private YearMonth period;
    private BigDecimal amount;
    private BigDecimal percentage;

    public Builder withPeriod(YearMonth yearMonth) {
      this.period = yearMonth;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withPercentage(BigDecimal percentage) {
      this.percentage = percentage;
      return this;
    }

    public TrendDto build() {
      return new TrendDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public YearMonth getPeriod() {
    return period;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getPercentage() {
    return percentage;
  }
}
