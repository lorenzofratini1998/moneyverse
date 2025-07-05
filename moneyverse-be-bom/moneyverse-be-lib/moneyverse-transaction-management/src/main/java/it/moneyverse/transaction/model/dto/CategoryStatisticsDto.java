package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryStatisticsDto.Builder.class)
public class CategoryStatisticsDto implements Serializable {
  private final UUID categoryId;
  private final PeriodDashboardDto period;
  private final BigDecimal amount;
  private final BigDecimal percentage;
  private final List<TrendDto> data;
  private final CategoryStatisticsDto previous;

  public CategoryStatisticsDto(Builder builder) {
    this.categoryId = builder.categoryId;
    this.period = builder.period;
    this.amount = builder.amount;
    this.percentage = builder.percentage;
    this.data = builder.data;
    this.previous = builder.previous;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID categoryId;
    private PeriodDashboardDto period;
    private BigDecimal amount;
    private BigDecimal percentage;
    private List<TrendDto> data;
    private CategoryStatisticsDto previous;

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withPeriod(PeriodDashboardDto period) {
      this.period = period;
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

    public Builder withData(List<TrendDto> data) {
      this.data = data;
      return this;
    }

    public Builder withPrevious(CategoryStatisticsDto previous) {
      this.previous = previous;
      return this;
    }

    public CategoryStatisticsDto build() {
      return new CategoryStatisticsDto(this);
    }
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public PeriodDashboardDto getPeriod() {
    return period;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public List<TrendDto> getData() {
    return data;
  }

  public BigDecimal getPercentage() {
    return percentage;
  }

  public CategoryStatisticsDto getPrevious() {
    return previous;
  }
}
