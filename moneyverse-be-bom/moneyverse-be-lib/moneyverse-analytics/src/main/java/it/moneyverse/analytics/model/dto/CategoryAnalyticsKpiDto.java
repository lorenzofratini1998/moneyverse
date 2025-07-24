package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryAnalyticsKpiDto.Builder.class)
public class CategoryAnalyticsKpiDto implements Serializable {
  private final PeriodDto period;
  private final UUID topCategory;
  private final CountDto activeCategories;
  private final UUID mostUsedCategory;
  private final AmountDto uncategorizedAmount;
  private final CategoryAnalyticsKpiDto compare;

  public CategoryAnalyticsKpiDto(Builder builder) {
    this.period = builder.period;
    this.topCategory = builder.topCategory;
    this.activeCategories = builder.activeCategories;
    this.mostUsedCategory = builder.mostUsedCategory;
    this.uncategorizedAmount = builder.uncategorizedAmount;
    this.compare = builder.compare;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private PeriodDto period;
    private UUID topCategory;
    private CountDto activeCategories;
    private UUID mostUsedCategory;
    private AmountDto uncategorizedAmount;
    private CategoryAnalyticsKpiDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withTopCategory(UUID topCategory) {
      this.topCategory = topCategory;
      return this;
    }

    public Builder withActiveCategories(CountDto activeCategories) {
      this.activeCategories = activeCategories;
      return this;
    }

    public Builder withMostUsedCategory(UUID mostUsedCategory) {
      this.mostUsedCategory = mostUsedCategory;
      return this;
    }

    public Builder withUncategorizedAmount(AmountDto uncategorizedAmount) {
      this.uncategorizedAmount = uncategorizedAmount;
      return this;
    }

    public Builder withCompare(CategoryAnalyticsKpiDto compare) {
      this.compare = compare;
      return this;
    }

    public CategoryAnalyticsKpiDto build() {
      return new CategoryAnalyticsKpiDto(this);
    }
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public UUID getTopCategory() {
    return topCategory;
  }

  public CountDto getActiveCategories() {
    return activeCategories;
  }

  public UUID getMostUsedCategory() {
    return mostUsedCategory;
  }

  public AmountDto getUncategorizedAmount() {
    return uncategorizedAmount;
  }

  public CategoryAnalyticsKpiDto getCompare() {
    return compare;
  }
}
