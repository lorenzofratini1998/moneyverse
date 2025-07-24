package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.CategoryAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsTrendProjection;
import it.moneyverse.analytics.model.repositories.CategoryAnalyticsDataAccess;
import it.moneyverse.analytics.services.processors.QueryDataProcessor;
import it.moneyverse.analytics.services.strategies.AnalyticsStrategy;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryAnalyticsClickhouseService implements CategoryAnalyticsService {

  private final CategoryAnalyticsDataAccess dataAccess;
  private final AnalyticsStrategy<CategoryAnalyticsKpiDto, CategoryAnalyticsKpiProjection>
      kpiStrategy;
  private final AnalyticsStrategy<
          List<CategoryAnalyticsDistributionDto>, List<CategoryAnalyticsDistributionProjection>>
      distributionStrategy;
  private final AnalyticsStrategy<
          List<CategoryAnalyticsTrendDto>, List<CategoryAnalyticsTrendProjection>>
      trendStrategy;

  public CategoryAnalyticsClickhouseService(
      CategoryAnalyticsDataAccess dataAccess,
      AnalyticsStrategy<CategoryAnalyticsKpiDto, CategoryAnalyticsKpiProjection> kpiStrategy,
      AnalyticsStrategy<
              List<CategoryAnalyticsDistributionDto>, List<CategoryAnalyticsDistributionProjection>>
          distributionStrategy,
      AnalyticsStrategy<List<CategoryAnalyticsTrendDto>, List<CategoryAnalyticsTrendProjection>>
          trendStrategy) {
    this.dataAccess = dataAccess;
    this.kpiStrategy = kpiStrategy;
    this.distributionStrategy = distributionStrategy;
    this.trendStrategy = trendStrategy;
  }

  @Override
  public CategoryAnalyticsKpiDto calculateKpi(FilterDto parameters) {
    List<CategoryAnalyticsKpiProjection> data = dataAccess.getKpiData(parameters);

    CategoryAnalyticsKpiProjection currentData =
        QueryDataProcessor.getCurrentData(data, CategoryAnalyticsKpiProjection::periodType);
    CategoryAnalyticsKpiProjection compareData =
        QueryDataProcessor.getCompareData(
            data, parameters, CategoryAnalyticsKpiProjection::periodType);
    return kpiStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public List<CategoryAnalyticsDistributionDto> calculateDistribution(FilterDto parameters) {
    List<CategoryAnalyticsDistributionProjection> data = dataAccess.getDistributionData(parameters);

    List<CategoryAnalyticsDistributionProjection> currentData =
        QueryDataProcessor.getCurrentDataList(
            data, CategoryAnalyticsDistributionProjection::periodType);
    List<CategoryAnalyticsDistributionProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, CategoryAnalyticsDistributionProjection::periodType);
    return distributionStrategy.calculate(currentData, compareData, parameters);
  }

  @Override
  public List<CategoryAnalyticsTrendDto> calculateTrend(FilterDto parameters) {
    List<CategoryAnalyticsTrendProjection> data = dataAccess.getTrendData(parameters);

    List<CategoryAnalyticsTrendProjection> currentData =
        QueryDataProcessor.getCurrentDataList(data, CategoryAnalyticsTrendProjection::periodType);
    List<CategoryAnalyticsTrendProjection> compareData =
        QueryDataProcessor.getCompareDataList(
            data, parameters, CategoryAnalyticsTrendProjection::periodType);
    return trendStrategy.calculate(currentData, compareData, parameters);
  }
}
