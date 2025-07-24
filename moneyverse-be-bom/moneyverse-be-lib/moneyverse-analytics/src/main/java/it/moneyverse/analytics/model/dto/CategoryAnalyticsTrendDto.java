package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryAnalyticsTrendDto.Builder.class)
public class CategoryAnalyticsTrendDto implements Serializable {
  private final PeriodDto period;
  private final UUID categoryId;
  private final List<AmountDto> data;
  private final CategoryAnalyticsTrendDto compare;

  public static class Builder {
    private PeriodDto period;
    private UUID categoryId;
    private List<AmountDto> data;
    private CategoryAnalyticsTrendDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withData(List<AmountDto> data) {
      this.data = data;
      return this;
    }

    public Builder withCompare(CategoryAnalyticsTrendDto compare) {
      this.compare = compare;
      return this;
    }

    public CategoryAnalyticsTrendDto build() {
      return new CategoryAnalyticsTrendDto(this);
    }
  }

  public CategoryAnalyticsTrendDto(Builder builder) {
    this.period = builder.period;
    this.categoryId = builder.categoryId;
    this.data = builder.data;
    this.compare = builder.compare;
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public List<AmountDto> getData() {
    return data;
  }

  public CategoryAnalyticsTrendDto getCompare() {
    return compare;
  }
}
