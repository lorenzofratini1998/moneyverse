package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;

public record TransactionAnalyticsDistributionProjection(
    String range,
    BigDecimal lowerBound,
    BigDecimal upperBound,
    Integer numberOfTransactions,
    QueryPeriodTypeEnum periodType) {}
