package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = DistributionRangeDto.Builder.class)
public class DistributionRangeDto {
  private final String range;
  private final CountDto count;

  public DistributionRangeDto(Builder builder) {
    this.range = builder.range;
    this.count = builder.count;
  }

  public static class Builder {
    private String range;
    private CountDto count;

    public Builder withRange(String range) {
      this.range = range;
      return this;
    }

    public Builder withCount(CountDto count) {
      this.count = count;
      return this;
    }

    public DistributionRangeDto build() {
      return new DistributionRangeDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getRange() {
    return range;
  }

  public CountDto getCount() {
    return count;
  }
}
