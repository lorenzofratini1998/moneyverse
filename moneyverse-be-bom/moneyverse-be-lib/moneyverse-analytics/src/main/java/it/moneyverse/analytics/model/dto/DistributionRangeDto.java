package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.model.dto.BoundCriteria;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = DistributionRangeDto.Builder.class)
public class DistributionRangeDto {
  private final BoundCriteria range;
  private final CountDto count;

  public DistributionRangeDto(Builder builder) {
    this.range = builder.range;
    this.count = builder.count;
  }

  public static class Builder {
    private BoundCriteria range;
    private CountDto count;

    public Builder withRange(BoundCriteria range) {
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

  public BoundCriteria getRange() {
    return range;
  }

  public CountDto getCount() {
    return count;
  }
}
