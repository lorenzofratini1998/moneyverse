package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsTrendDto;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsTrendProjection;
import it.moneyverse.analytics.model.repositories.TransactionAnalyticsDataAccess;
import it.moneyverse.analytics.services.processors.QueryDataProcessor;
import it.moneyverse.analytics.services.strategies.AnalyticsStrategy;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionAnalyticsClickhouseService implements TransactionAnalyticsService {

  private final TransactionAnalyticsDataAccess dataAccess;
  private final AnalyticsStrategy<TransactionAnalyticsKpiDto, TransactionAnalyticsKpiProjection>
      kpiStrategy;
  private final AnalyticsStrategy<
          TransactionAnalyticsDistributionDto, List<TransactionAnalyticsDistributionProjection>>
      distributionStrategy;
  private final AnalyticsStrategy<
          TransactionAnalyticsTrendDto, List<TransactionAnalyticsTrendProjection>>
      trendStrategy;

  public TransactionAnalyticsClickhouseService(
      TransactionAnalyticsDataAccess dataAccess,
      AnalyticsStrategy<TransactionAnalyticsKpiDto, TransactionAnalyticsKpiProjection> kpiStrategy,
      AnalyticsStrategy<
              TransactionAnalyticsDistributionDto, List<TransactionAnalyticsDistributionProjection>>
          distributionStrategy,
      AnalyticsStrategy<TransactionAnalyticsTrendDto, List<TransactionAnalyticsTrendProjection>>
          trendStrategy) {
    this.dataAccess = dataAccess;
    this.kpiStrategy = kpiStrategy;
    this.distributionStrategy = distributionStrategy;
    this.trendStrategy = trendStrategy;
  }

  @Override
  public TransactionAnalyticsKpiDto calculateKpi(FilterDto parameters) {
    List<TransactionAnalyticsKpiProjection> data = dataAccess.getKpiData(parameters);

    TransactionAnalyticsKpiProjection currentData =
        QueryDataProcessor.getCurrentData(data, TransactionAnalyticsKpiProjection::periodType);
    TransactionAnalyticsKpiProjection compareData =
        QueryDataProcessor.getCompareData(
            data, parameters, TransactionAnalyticsKpiProjection::periodType);
    return kpiStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public TransactionAnalyticsDistributionDto calculateDistribution(FilterDto parameters) {
    List<TransactionAnalyticsDistributionProjection> data =
        dataAccess.getDistributionData(parameters);

    List<TransactionAnalyticsDistributionProjection> currentData =
        QueryDataProcessor.getCurrentDataList(
            data, TransactionAnalyticsDistributionProjection::periodType);
    List<TransactionAnalyticsDistributionProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, TransactionAnalyticsDistributionProjection::periodType);
    return distributionStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public TransactionAnalyticsTrendDto calculateTrend(FilterDto parameters) {
    List<TransactionAnalyticsTrendProjection> data = dataAccess.getTrendData(parameters);

    List<TransactionAnalyticsTrendProjection> currentData =
        QueryDataProcessor.getCurrentDataList(
            data, TransactionAnalyticsTrendProjection::periodType);
    List<TransactionAnalyticsTrendProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, TransactionAnalyticsTrendProjection::periodType);
    return trendStrategy.calculate(currentData, compareData, parameters);
  }
}
