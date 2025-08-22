package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsDistributionProjection;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryAnalyticsDistributionQuery
    extends AbstractFilterQuery<CategoryAnalyticsDistributionProjection> {

  private static class Columns {
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String TOTAL_EXPENSE = "TOTAL_EXPENSE";
    public static final String TOTAL_INCOME = "TOTAL_INCOME";
    public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    public static final String PERIOD_TYPE = "PERIOD_TYPE";
  }

  @Override
  public String getSql() {
    return
"""
    WITH filtered_transactions AS (
     SELECT
        argMax(t.EVENT_ID, t.EVENT_TIMESTAMP) AS EVENT_ID,
        argMax(t.EVENT_TYPE, t.EVENT_TIMESTAMP) AS EVENT_TYPE,
        argMax(t.USER_ID, t.EVENT_TIMESTAMP) AS USER_ID,
        argMax(t.TRANSACTION_ID, t.EVENT_TIMESTAMP) AS TRANSACTION_ID,
        argMax(t.ACCOUNT_ID, t.EVENT_TIMESTAMP) AS ACCOUNT_ID,
        argMax(t.CATEGORY_ID, t.EVENT_TIMESTAMP) AS CATEGORY_ID,
        argMax(t.BUDGET_ID, t.EVENT_TIMESTAMP) AS BUDGET_ID,
        argMax(t.TAGS, t.EVENT_TIMESTAMP) AS TAGS,
        argMax(t.AMOUNT, t.EVENT_TIMESTAMP) AS AMOUNT,
        argMax(t.NORMALIZED_AMOUNT, t.EVENT_TIMESTAMP) AS NORMALIZED_AMOUNT,
        argMax(t.CURRENCY, t.EVENT_TIMESTAMP) AS CURRENCY,
        argMax(t.DATE, t.EVENT_TIMESTAMP) AS DATE
     FROM TRANSACTION_EVENTS t
     WHERE t.USER_ID = :userId
       AND (
           t.DATE BETWEEN :startDate AND :endDate
           OR (:hasComparePeriod = 1 AND t.DATE BETWEEN :compareStartDate AND :compareEndDate)
       )
       AND (empty([:accounts]) OR t.ACCOUNT_ID IN [:accounts])
       AND (empty([:categories]) OR t.CATEGORY_ID IN [:categories])
       AND (empty([:tags]) OR hasAny(t.TAGS, arrayMap(x -> toUUID(x), [:tags])))
       AND (empty(:currency) OR t.CURRENCY = :currency)
       AND t.EVENT_TYPE != 2
     GROUP BY coalesce(t.ORIGINAL_TRANSACTION_ID, t.TRANSACTION_ID)
    )

    SELECT
        CATEGORY_ID,
        abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS TOTAL_EXPENSE,
        sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
        sum(NORMALIZED_AMOUNT) AS TOTAL_AMOUNT,
        'CURRENT' AS PERIOD_TYPE
    FROM filtered_transactions
    WHERE CATEGORY_ID IS NOT NULL AND DATE BETWEEN :startDate AND :endDate
    GROUP BY CATEGORY_ID, PERIOD_TYPE

    UNION ALL

    SELECT
        CATEGORY_ID,
        abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS TOTAL_EXPENSE,
        sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
        sum(NORMALIZED_AMOUNT) AS TOTAL_AMOUNT,
        'COMPARE' AS PERIOD_TYPE
    FROM filtered_transactions
    WHERE CATEGORY_ID IS NOT NULL AND :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
    GROUP BY CATEGORY_ID, PERIOD_TYPE
""";
  }

  @Override
  public RowMapper<CategoryAnalyticsDistributionProjection> getRowMapper() {
    return (rs, rowNum) ->
        new CategoryAnalyticsDistributionProjection(
            UUID.fromString(rs.getString(Columns.CATEGORY_ID)),
            rs.getBigDecimal(Columns.TOTAL_EXPENSE),
            rs.getBigDecimal(Columns.TOTAL_INCOME),
            rs.getBigDecimal(Columns.TOTAL_AMOUNT),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE)));
  }
}
