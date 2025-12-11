package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionAnalyticsTrendProjection(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal expense,
    BigDecimal income,
    QueryPeriodTypeEnum periodType) {}
