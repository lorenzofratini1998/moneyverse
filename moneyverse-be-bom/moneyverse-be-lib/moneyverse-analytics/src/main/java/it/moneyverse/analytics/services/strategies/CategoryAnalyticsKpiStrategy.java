package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
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
    return AnalyticsUtils.getCount(current, null, CategoryAnalyticsKpiProjection::activeCategories);
  }

  private CountDto getCount(
      CategoryAnalyticsKpiProjection current, CategoryAnalyticsKpiProjection compare) {
    return AnalyticsUtils.getCount(
        current, compare, CategoryAnalyticsKpiProjection::activeCategories);
  }

  private AmountDto getAmount(CategoryAnalyticsKpiProjection current) {
    return AnalyticsUtils.getAmount(current, CategoryAnalyticsKpiProjection::uncategorizedAmount);
  }

  private AmountDto getAmount(
      CategoryAnalyticsKpiProjection current, CategoryAnalyticsKpiProjection compare) {
    return AnalyticsUtils.getAmount(
        current, compare, CategoryAnalyticsKpiProjection::uncategorizedAmount);
  }
}
