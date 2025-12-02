package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AmountDto.Builder.class)
public class AmountDto implements Serializable {
  private final PeriodDto period;
  private final BigDecimal amount;
  private final BigDecimal variation;

  public AmountDto(Builder builder) {
    this.period = builder.period;
    this.amount = builder.amount;
    this.variation = builder.variation;
  }

  public static class Builder {
    private PeriodDto period;
    private BigDecimal amount;
    private BigDecimal variation;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withVariation(BigDecimal variation) {
      this.variation = variation;
      return this;
    }

    public AmountDto build() {
      return new AmountDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getVariation() {
    return variation;
  }
}
