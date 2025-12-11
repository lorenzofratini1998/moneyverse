package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;

public record TransactionAnalyticsKpiProjection(
    Integer numberOfTransactions,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal averageAmount,
    BigDecimal quantile90,
    QueryPeriodTypeEnum periodType) {}
