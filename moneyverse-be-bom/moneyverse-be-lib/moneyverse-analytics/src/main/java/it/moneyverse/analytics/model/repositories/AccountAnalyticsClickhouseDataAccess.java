package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsAmountDistributionProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import it.moneyverse.analytics.model.projections.AccountAnalyticsTrendProjection;
import it.moneyverse.analytics.model.queries.AccountAnalyticsAmountDistributionQuery;
import it.moneyverse.analytics.model.queries.AccountAnalyticsKpiQuery;
import it.moneyverse.analytics.model.queries.AccountAnalyticsTrendQuery;
import it.moneyverse.analytics.services.QueryExecutor;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AccountAnalyticsClickhouseDataAccess implements AccountAnalyticsDataAccess {

  private final QueryExecutor queryExecutor;
  private final AccountAnalyticsKpiQuery kpiQuery;
  private final AccountAnalyticsAmountDistributionQuery amountDistributionQuery;
  private final AccountAnalyticsTrendQuery trendQuery;

  public AccountAnalyticsClickhouseDataAccess(
      QueryExecutor queryExecutor,
      AccountAnalyticsKpiQuery kpiQuery,
      AccountAnalyticsAmountDistributionQuery amountDistributionQuery,
      AccountAnalyticsTrendQuery trendQuery) {
    this.queryExecutor = queryExecutor;
    this.kpiQuery = kpiQuery;
    this.amountDistributionQuery = amountDistributionQuery;
    this.trendQuery = trendQuery;
  }

  @Override
  public List<AccountAnalyticsKpiProjection> getKpiData(FilterDto parameters) {
    return queryExecutor.execute(kpiQuery, parameters);
  }

  @Override
  public List<AccountAnalyticsAmountDistributionProjection> getAmountDistributionData(
      FilterDto parameters) {
    return queryExecutor.execute(amountDistributionQuery, parameters);
  }

  @Override
  public List<AccountAnalyticsTrendProjection> getTrendData(FilterDto parameters) {
    return queryExecutor.execute(trendQuery, parameters);
  }
}
