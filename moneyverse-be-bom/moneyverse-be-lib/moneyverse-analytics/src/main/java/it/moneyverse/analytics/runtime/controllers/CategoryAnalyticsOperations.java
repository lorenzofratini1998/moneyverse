package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.CategoryAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import jakarta.validation.Valid;
import java.util.List;

public interface CategoryAnalyticsOperations {
  CategoryAnalyticsKpiDto calculateCategoryKpi(@Valid FilterDto filter);

  List<CategoryAnalyticsDistributionDto> calculateCategoryDistribution(@Valid FilterDto filter);

  List<CategoryAnalyticsTrendDto> calculateCategoryTrend(@Valid FilterDto filter);
}
