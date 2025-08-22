package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountAnalyticsKpiProjection(
    BigDecimal totalExpense,
    BigDecimal totalIncome,
    BigDecimal totalAmount,
    Integer activeAccounts,
    UUID mostUsedAccount,
    UUID leastUsedAccount,
    QueryPeriodTypeEnum periodType) {}
