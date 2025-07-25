package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsTrendProjection;
import java.util.List;

public interface TransactionAnalyticsDataAccess {
  List<TransactionAnalyticsKpiProjection> getKpiData(FilterDto parameters);

  List<TransactionAnalyticsDistributionProjection> getDistributionData(FilterDto parameters);

  List<TransactionAnalyticsTrendProjection> getTrendData(FilterDto parameters);
}
