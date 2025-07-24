package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.CategoryAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import java.util.List;

public interface CategoryAnalyticsService {
  CategoryAnalyticsKpiDto calculateKpi(FilterDto parameters);

  List<CategoryAnalyticsDistributionDto> calculateDistribution(FilterDto parameters);

  List<CategoryAnalyticsTrendDto> calculateTrend(FilterDto parameters);
}
