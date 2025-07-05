package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryDashboardDto.Builder.class)
public class CategoryDashboardDto {
  private final PeriodDashboardDto period;
  private final PeriodDashboardDto comparePeriod;
  private final CategoryKPIDto kpi;
  private final CategoryStatisticsDto topCategory;
  private final List<CategoryStatisticsDto> categories;

  public CategoryDashboardDto(Builder builder) {
    this.period = builder.period;
    this.comparePeriod = builder.comparePeriod;
    this.kpi = builder.kpi;
    this.topCategory = builder.topCategory;
    this.categories = builder.categories;
  }

  public static class Builder {
    private PeriodDashboardDto period;
    private PeriodDashboardDto comparePeriod;
    private CategoryKPIDto kpi;
    private CategoryStatisticsDto topCategory;
    private List<CategoryStatisticsDto> categories;

    public Builder withPeriod(PeriodDashboardDto period) {
      this.period = period;
      return this;
    }

    public Builder withComparePeriod(PeriodDashboardDto comparePeriod) {
      this.comparePeriod = comparePeriod;
      return this;
    }

    public Builder withKpi(CategoryKPIDto kpi) {
      this.kpi = kpi;
      return this;
    }

    public Builder withTopCategory(CategoryStatisticsDto topCategory) {
      this.topCategory = topCategory;
      return this;
    }

    public Builder withCategories(List<CategoryStatisticsDto> categories) {
      this.categories = categories;
      return this;
    }

    public CategoryDashboardDto build() {
      return new CategoryDashboardDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PeriodDashboardDto getPeriod() {
    return period;
  }

  public PeriodDashboardDto getComparePeriod() {
    return comparePeriod;
  }

  public CategoryKPIDto getKpi() {
    return kpi;
  }

  public CategoryStatisticsDto getTopCategory() {
    return topCategory;
  }

  public List<CategoryStatisticsDto> getCategories() {
    return categories;
  }
}
