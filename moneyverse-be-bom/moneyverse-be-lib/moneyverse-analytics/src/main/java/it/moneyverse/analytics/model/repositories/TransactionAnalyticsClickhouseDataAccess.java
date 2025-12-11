package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsDistributionProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsTrendProjection;
import it.moneyverse.analytics.model.queries.TransactionAnalyticsDistributionQuery;
import it.moneyverse.analytics.model.queries.TransactionAnalyticsKpiQuery;
import it.moneyverse.analytics.model.queries.TransactionAnalyticsTrendQuery;
import it.moneyverse.analytics.services.QueryExecutor;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionAnalyticsClickhouseDataAccess implements TransactionAnalyticsDataAccess {

  private final QueryExecutor queryExecutor;
  private final TransactionAnalyticsKpiQuery kpiQuery;
  private final TransactionAnalyticsDistributionQuery distributionQuery;
  private final TransactionAnalyticsTrendQuery trendQuery;

  public TransactionAnalyticsClickhouseDataAccess(
      QueryExecutor queryExecutor,
      TransactionAnalyticsKpiQuery kpiQuery,
      TransactionAnalyticsDistributionQuery distributionQuery,
      TransactionAnalyticsTrendQuery trendQuery) {
    this.queryExecutor = queryExecutor;
    this.kpiQuery = kpiQuery;
    this.distributionQuery = distributionQuery;
    this.trendQuery = trendQuery;
  }

  @Override
  public List<TransactionAnalyticsKpiProjection> getKpiData(FilterDto parameters) {
    return queryExecutor.execute(kpiQuery, parameters);
  }

  @Override
  public List<TransactionAnalyticsDistributionProjection> getDistributionData(
      FilterDto parameters) {
    return queryExecutor.execute(distributionQuery, parameters);
  }

  @Override
  public List<TransactionAnalyticsTrendProjection> getTrendData(FilterDto parameters) {
    return queryExecutor.execute(trendQuery, parameters);
  }
}
