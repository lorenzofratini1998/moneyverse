package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsTrendProjection;
import it.moneyverse.analytics.model.queries.CategoryAnalyticsDistributionQuery;
import it.moneyverse.analytics.model.queries.CategoryAnalyticsKpiQuery;
import it.moneyverse.analytics.model.queries.CategoryAnalyticsTrendQuery;
import it.moneyverse.analytics.services.QueryExecutor;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryAnalyticsClickhouseDataAccess implements CategoryAnalyticsDataAccess {

  private final QueryExecutor queryExecutor;
  private final CategoryAnalyticsKpiQuery kpiQuery;
  private final CategoryAnalyticsDistributionQuery distributionQuery;
  private final CategoryAnalyticsTrendQuery trendQuery;

  public CategoryAnalyticsClickhouseDataAccess(
      QueryExecutor queryExecutor,
      CategoryAnalyticsKpiQuery kpiQuery,
      CategoryAnalyticsDistributionQuery distributionQuery,
      CategoryAnalyticsTrendQuery trendQuery) {
    this.queryExecutor = queryExecutor;
    this.kpiQuery = kpiQuery;
    this.distributionQuery = distributionQuery;
    this.trendQuery = trendQuery;
  }

  @Override
  public List<CategoryAnalyticsKpiProjection> getKpiData(FilterDto parameters) {
    return queryExecutor.execute(kpiQuery, parameters);
  }

  @Override
  public List<CategoryAnalyticsDistributionProjection> getDistributionData(FilterDto parameters) {
    return queryExecutor.execute(distributionQuery, parameters);
  }

  @Override
  public List<CategoryAnalyticsTrendProjection> getTrendData(FilterDto parameters) {
    return queryExecutor.execute(trendQuery, parameters);
  }
}
