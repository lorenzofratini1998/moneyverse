package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ActiveResourceDto.Builder.class)
public class ActiveResourceDto {
  private final Integer numberOfActiveResources;
  private final Integer trend;

  public ActiveResourceDto(Builder builder) {
    this.numberOfActiveResources = builder.numberOfActiveResources;
    this.trend = builder.trend;
  }

  public static class Builder {
    private Integer numberOfActiveResources;
    private Integer trend;

    public Builder withNumberOfActiveResources(Integer numberOfActiveResources) {
      this.numberOfActiveResources = numberOfActiveResources;
      return this;
    }

    public Builder withTrend(Integer trend) {
      this.trend = trend;
      return this;
    }

    public ActiveResourceDto build() {
      return new ActiveResourceDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public Integer getNumberOfActiveResources() {
    return numberOfActiveResources;
  }

  public Integer getTrend() {
    return trend;
  }
}
