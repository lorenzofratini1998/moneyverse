package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryKPIDto.Builder.class)
public class CategoryKPIDto implements Serializable {
  private final PeriodDashboardDto period;
  private final BigDecimal totalAmount;
  private final Integer numberOfActiveCategories;
  private final BigDecimal averageAmount;
  private final CategoryKPIDto previous;

  public CategoryKPIDto(Builder builder) {
    this.period = builder.period;
    this.totalAmount = builder.totalAmount;
    this.numberOfActiveCategories = builder.numberOfActiveCategories;
    this.averageAmount = builder.averageAmount;
    this.previous = builder.previous;
  }

  public static class Builder {
    private PeriodDashboardDto period;
    private BigDecimal totalAmount;
    private Integer numberOfActiveCategories;
    private BigDecimal averageAmount;
    private CategoryKPIDto previous;

    public Builder withPeriod(PeriodDashboardDto period) {
      this.period = period;
      return this;
    }

    public Builder withTotalAmount(BigDecimal totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder withNumberOfActiveCategories(Integer numberOfActiveCategories) {
      this.numberOfActiveCategories = numberOfActiveCategories;
      return this;
    }

    public Builder withAverageAmount(BigDecimal averageAmount) {
      this.averageAmount = averageAmount;
      return this;
    }

    public Builder withPrevious(CategoryKPIDto previous) {
      this.previous = previous;
      return this;
    }

    public CategoryKPIDto build() {
      return new CategoryKPIDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDashboardDto getPeriod() {
    return period;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public Integer getNumberOfActiveCategories() {
    return numberOfActiveCategories;
  }

  public BigDecimal getAverageAmount() {
    return averageAmount;
  }

  public CategoryKPIDto getPrevious() {
    return previous;
  }
}
