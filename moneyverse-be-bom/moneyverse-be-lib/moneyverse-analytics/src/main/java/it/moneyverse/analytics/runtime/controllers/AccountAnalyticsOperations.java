package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import jakarta.validation.Valid;

import java.util.List;

public interface AccountAnalyticsOperations {
  AccountAnalyticsKpiDto calculateAccountKpi(@Valid FilterDto filter);

  List<AccountAnalyticsDistributionDto> calculateAccountDistribution(@Valid FilterDto filter);

  List<AccountAnalyticsTrendDto> calculateAccountTrend(@Valid FilterDto filter);
}
