package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;

public record TransactionAnalyticsDistributionProjection(
    String range, Integer numberOfTransactions, QueryPeriodTypeEnum periodType) {}
