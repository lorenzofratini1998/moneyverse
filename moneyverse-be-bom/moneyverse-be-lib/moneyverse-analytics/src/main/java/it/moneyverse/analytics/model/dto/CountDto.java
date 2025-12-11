package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CountDto.Builder.class)
public class CountDto {
  private final Integer count;
  private final Integer variation;

  public static class Builder {
    private Integer count;
    private Integer variation;

    public Builder withCount(Integer count) {
      this.count = count;
      return this;
    }

    public Builder withVariation(Integer variation) {
      this.variation = variation;
      return this;
    }

    public CountDto build() {
      return new CountDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private CountDto(Builder builder) {
    this.count = builder.count;
    this.variation = builder.variation;
  }

  public Integer getCount() {
    return count;
  }

  public Integer getVariation() {
    return variation;
  }
}
