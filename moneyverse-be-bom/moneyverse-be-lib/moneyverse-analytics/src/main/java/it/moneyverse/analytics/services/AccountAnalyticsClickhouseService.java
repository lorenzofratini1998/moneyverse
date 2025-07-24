package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsAmountDistributionProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsTrendProjection;
import it.moneyverse.analytics.model.repositories.AccountAnalyticsDataAccess;
import it.moneyverse.analytics.services.processors.QueryDataProcessor;
import it.moneyverse.analytics.services.strategies.AnalyticsStrategy;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountAnalyticsClickhouseService implements AccountAnalyticsService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AccountAnalyticsClickhouseService.class);

  private final AccountAnalyticsDataAccess dataAccess;
  private final AnalyticsStrategy<AccountAnalyticsKpiDto, AccountAnalyticsKpiProjection>
      kpiCalculationStrategy;
  private final AnalyticsStrategy<
          List<AccountAnalyticsDistributionDto>, List<AccountAnalyticsAmountDistributionProjection>>
      distributionCalculationStrategy;
  private final AnalyticsStrategy<
          List<AccountAnalyticsTrendDto>, List<AccountAnalyticsTrendProjection>>
      trendStrategy;

  public AccountAnalyticsClickhouseService(
      AccountAnalyticsDataAccess dataAccess,
      AnalyticsStrategy<AccountAnalyticsKpiDto, AccountAnalyticsKpiProjection>
          kpiCalculationStrategy,
      AnalyticsStrategy<
              List<AccountAnalyticsDistributionDto>,
              List<AccountAnalyticsAmountDistributionProjection>>
          distributionCalculationStrategy,
      AnalyticsStrategy<List<AccountAnalyticsTrendDto>, List<AccountAnalyticsTrendProjection>>
          trendStrategy) {
    this.dataAccess = dataAccess;
    this.kpiCalculationStrategy = kpiCalculationStrategy;
    this.distributionCalculationStrategy = distributionCalculationStrategy;
    this.trendStrategy = trendStrategy;
  }

  @Override
  public AccountAnalyticsKpiDto calculateKpi(FilterDto parameters) {
    List<AccountAnalyticsKpiProjection> data = dataAccess.getKpiData(parameters);

    AccountAnalyticsKpiProjection currentData =
        QueryDataProcessor.getCurrentData(data, AccountAnalyticsKpiProjection::periodType);

    AccountAnalyticsKpiProjection compareData =
        QueryDataProcessor.getCompareData(
            data, parameters, AccountAnalyticsKpiProjection::periodType);

    return kpiCalculationStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public List<AccountAnalyticsDistributionDto> calculateDistribution(FilterDto parameters) {
    List<AccountAnalyticsAmountDistributionProjection> data =
        dataAccess.getAmountDistributionData(parameters);

    List<AccountAnalyticsAmountDistributionProjection> currentData =
        QueryDataProcessor.getCurrentDataList(
            data, AccountAnalyticsAmountDistributionProjection::periodType);

    List<AccountAnalyticsAmountDistributionProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, AccountAnalyticsAmountDistributionProjection::periodType);

    return distributionCalculationStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public List<AccountAnalyticsTrendDto> calculateTrend(FilterDto parameters) {
    List<AccountAnalyticsTrendProjection> data = dataAccess.getTrendData(parameters);

    List<AccountAnalyticsTrendProjection> currentData =
        QueryDataProcessor.getCurrentDataList(data, AccountAnalyticsTrendProjection::periodType);

    List<AccountAnalyticsTrendProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, AccountAnalyticsTrendProjection::periodType);

    return trendStrategy.calculate(currentData, compareData, parameters);
  }
}
