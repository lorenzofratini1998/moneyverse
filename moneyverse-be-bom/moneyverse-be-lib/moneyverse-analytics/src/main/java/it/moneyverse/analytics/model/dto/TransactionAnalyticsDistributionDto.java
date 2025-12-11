package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransactionAnalyticsDistributionDto.Builder.class)
public class TransactionAnalyticsDistributionDto {
  private final PeriodDto period;
  private final List<DistributionRangeDto> data;
  private final TransactionAnalyticsDistributionDto compare;

  public TransactionAnalyticsDistributionDto(Builder builder) {
    this.period = builder.period;
    this.data = builder.data;
    this.compare = builder.compare;
  }

  public static class Builder {
    private PeriodDto period;
    private List<DistributionRangeDto> data;
    private TransactionAnalyticsDistributionDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withData(List<DistributionRangeDto> data) {
      this.data = data;
      return this;
    }

    public Builder withCompare(TransactionAnalyticsDistributionDto compare) {
      this.compare = compare;
      return this;
    }

    public TransactionAnalyticsDistributionDto build() {
      return new TransactionAnalyticsDistributionDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public List<DistributionRangeDto> getData() {
    return data;
  }

  public TransactionAnalyticsDistributionDto getCompare() {
    return compare;
  }
}
