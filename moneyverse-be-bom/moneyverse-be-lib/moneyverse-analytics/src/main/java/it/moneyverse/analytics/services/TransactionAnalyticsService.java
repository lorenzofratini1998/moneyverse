package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsTrendDto;

public interface TransactionAnalyticsService {
  TransactionAnalyticsKpiDto calculateKpi(FilterDto parameters);

  TransactionAnalyticsDistributionDto calculateDistribution(FilterDto parameters);

  TransactionAnalyticsTrendDto calculateTrend(FilterDto parameters);
}
