package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsTrendDto;
import jakarta.validation.Valid;

public interface TransactionAnalyticsOperations {
  TransactionAnalyticsKpiDto calculateTransactionKpi(@Valid FilterDto filter);

  TransactionAnalyticsDistributionDto calculateTransactionDistribution(@Valid FilterDto filter);

  TransactionAnalyticsTrendDto calculateTransactionTrend(@Valid FilterDto filter);
}
