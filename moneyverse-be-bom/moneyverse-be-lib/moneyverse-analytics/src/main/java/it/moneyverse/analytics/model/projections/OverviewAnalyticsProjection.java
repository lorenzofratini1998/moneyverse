package it.moneyverse.analytics.model.projections;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverviewAnalyticsProjection(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal totalAmount) {}
