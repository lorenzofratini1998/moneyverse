package it.moneyverse.analytics.model.projections;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record CategoryAnalyticsKpiProjection(
    UUID topCategory,
    Integer activeCategories,
    UUID mostUsedCategory,
    BigDecimal uncategorizedAmount,
    QueryPeriodTypeEnum periodType)
    implements Serializable {}
