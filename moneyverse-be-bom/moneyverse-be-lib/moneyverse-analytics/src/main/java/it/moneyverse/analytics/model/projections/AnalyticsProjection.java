package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;

public class AnalyticsProjection {
  private final QueryPeriodTypeEnum periodType;

  public AnalyticsProjection(QueryPeriodTypeEnum periodType) {
    this.periodType = periodType;
  }

  public QueryPeriodTypeEnum getPeriodType() {
    return periodType;
  }
}
