package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import java.util.List;

public interface AccountAnalyticsService {
  AccountAnalyticsKpiDto calculateKpi(FilterDto parameters);

  List<AccountAnalyticsDistributionDto> calculateDistribution(FilterDto parameters);

  List<AccountAnalyticsTrendDto> calculateTrend(FilterDto parameters);
}
