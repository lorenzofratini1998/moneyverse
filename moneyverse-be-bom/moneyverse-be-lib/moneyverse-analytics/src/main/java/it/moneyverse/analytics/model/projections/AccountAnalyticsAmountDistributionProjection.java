package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountAnalyticsAmountDistributionProjection(
    UUID accountId,
    BigDecimal totalExpense,
    BigDecimal totalIncome,
    BigDecimal totalAmount,
    QueryPeriodTypeEnum periodType) {}
