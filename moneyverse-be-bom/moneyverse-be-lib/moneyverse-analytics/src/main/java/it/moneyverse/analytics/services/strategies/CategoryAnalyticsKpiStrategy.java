package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CategoryAnalyticsKpiStrategy
    implements AnalyticsStrategy<CategoryAnalyticsKpiDto, CategoryAnalyticsKpiProjection> {

  @Override
  public CategoryAnalyticsKpiDto calculate(
      CategoryAnalyticsKpiProjection currentData,
      CategoryAnalyticsKpiProjection compareData,
      FilterDto parameters) {
    CategoryAnalyticsKpiDto compare =
        parameters.comparePeriod() != null
            ? CategoryAnalyticsKpiDto.builder()
                .withPeriod(parameters.comparePeriod())
                .withTopCategory(compareData.topCategory())
                .withActiveCategories(getCount(compareData))
                .withMostUsedCategory(compareData.mostUsedCategory())
                .withUncategorizedAmount(getAmount(compareData))
                .build()
            : null;

    return CategoryAnalyticsKpiDto.builder()
        .withPeriod(parameters.period())
        .withTopCategory(currentData.topCategory())
        .withActiveCategories(getCount(currentData, compareData))
        .withMostUsedCategory(currentData.mostUsedCategory())
        .withUncategorizedAmount(getAmount(currentData, compareData))
        .withCompare(compare)
        .build();
  }

  private CountDto getCount(CategoryAnalyticsKpiProjection current) {
    return getCount(current, null);
  }

  private CountDto getCount(
      CategoryAnalyticsKpiProjection current, CategoryAnalyticsKpiProjection compare) {
    Integer currentCount = current.activeCategories();
    Integer compareCount = compare != null ? compare.activeCategories() : null;
    Integer variation = null;

    if (compareCount != null && compareCount != 0) {
      variation = currentCount - compareCount;
    }

    return CountDto.builder().withCount(currentCount).withVariation(variation).build();
  }

  private AmountDto getAmount(CategoryAnalyticsKpiProjection current) {
    return getAmount(current, null);
  }

  private AmountDto getAmount(
      CategoryAnalyticsKpiProjection current, CategoryAnalyticsKpiProjection compare) {
    BigDecimal currentAmount = current.uncategorizedAmount();
    BigDecimal compareAmount = compare != null ? compare.uncategorizedAmount() : null;
    BigDecimal variation = null;

    if (compareAmount != null && !compareAmount.equals(BigDecimal.ZERO)) {
      variation = AnalyticsUtils.calculateTrend(currentAmount, compareAmount);
    }

    return AmountDto.builder().withAmount(currentAmount).withVariation(variation).build();
  }
}
