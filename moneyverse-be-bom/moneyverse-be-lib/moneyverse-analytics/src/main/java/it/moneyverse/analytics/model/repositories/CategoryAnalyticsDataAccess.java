package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsTrendProjection;
import java.util.List;

public interface CategoryAnalyticsDataAccess {
  List<CategoryAnalyticsKpiProjection> getKpiData(FilterDto parameters);

  List<CategoryAnalyticsDistributionProjection> getDistributionData(FilterDto parameters);

  List<CategoryAnalyticsTrendProjection> getTrendData(FilterDto parameters);
}
