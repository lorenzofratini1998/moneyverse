package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CategoryAnalyticsTrendProjection(
    UUID categoryId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalAmount,
    QueryPeriodTypeEnum periodType) {}
