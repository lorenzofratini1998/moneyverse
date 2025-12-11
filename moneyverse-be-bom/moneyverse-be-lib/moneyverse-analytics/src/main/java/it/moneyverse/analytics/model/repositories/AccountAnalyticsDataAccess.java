package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsAmountDistributionProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsTrendProjection;
import java.util.List;

public interface AccountAnalyticsDataAccess {
  List<AccountAnalyticsKpiProjection> getKpiData(FilterDto parameters);

  List<AccountAnalyticsAmountDistributionProjection> getAmountDistributionData(
      FilterDto parameters);

  List<AccountAnalyticsTrendProjection> getTrendData(FilterDto parameters);
}
