package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsKpiProjection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryAnalyticsKpiQuery extends AbstractFilterQuery<CategoryAnalyticsKpiProjection> {

  private static class Columns {
    public static final String TOP_CATEGORY = "TOP_CATEGORY";
    public static final String ACTIVE_CATEGORIES = "ACTIVE_CATEGORIES";
    public static final String MOST_USED_CATEGORY = "MOST_USED_CATEGORY";
    public static final String UNCATEGORIZED_AMOUNT = "UNCATEGORIZED_AMOUNT";
    public static final String PERIOD_TYPE = "PERIOD_TYPE";

    private Columns() {}
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
        ),
        current_category_stats AS (
            SELECT
                CATEGORY_ID,
                sum(NORMALIZED_AMOUNT) AS total_amount,
                count() AS transactions_count
            FROM filtered_transactions
            WHERE DATE BETWEEN :startDate AND :endDate
            GROUP BY CATEGORY_ID
            HAVING transactions_count > 0
        ),
        compare_category_stats AS (
            SELECT
                CATEGORY_ID,
                sum(NORMALIZED_AMOUNT) AS total_amount,
                count() AS transactions_count
            FROM filtered_transactions
            WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
            GROUP BY CATEGORY_ID
            HAVING transactions_count > 0
        )

        SELECT
            (SELECT CATEGORY_ID FROM current_category_stats WHERE CATEGORY_ID IS NOT NULL ORDER BY total_amount DESC LIMIT 1) AS TOP_CATEGORY,
            uniqExact(CATEGORY_ID) AS ACTIVE_CATEGORIES,
            (SELECT CATEGORY_ID FROM current_category_stats WHERE CATEGORY_ID IS NOT NULL ORDER BY transactions_count DESC LIMIT 1) AS MOST_USED_CATEGORY,
            sumIf(NORMALIZED_AMOUNT, CATEGORY_ID IS NULL) AS UNCATEGORIZED_AMOUNT,
            'CURRENT' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE DATE BETWEEN :startDate AND :endDate

        UNION ALL

        SELECT
            (SELECT CATEGORY_ID FROM compare_category_stats WHERE CATEGORY_ID IS NOT NULL ORDER BY total_amount DESC LIMIT 1) AS TOP_CATEGORY,
            uniqExact(CATEGORY_ID) AS ACTIVE_CATEGORIES,
            (SELECT CATEGORY_ID FROM compare_category_stats WHERE CATEGORY_ID IS NOT NULL ORDER BY transactions_count DESC LIMIT 1) AS MOST_USED_CATEGORY,
            sumIf(NORMALIZED_AMOUNT, CATEGORY_ID IS NULL) AS UNCATEGORIZED_AMOUNT,
            'COMPARE' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
""";
  }

  @Override
  public RowMapper<CategoryAnalyticsKpiProjection> getRowMapper() {
    return (rs, rowNum) ->
        new CategoryAnalyticsKpiProjection(
            Optional.ofNullable(rs.getString(Columns.TOP_CATEGORY))
                .map(UUID::fromString)
                .orElse(null),
            rs.getInt(Columns.ACTIVE_CATEGORIES),
            Optional.ofNullable(rs.getString(Columns.MOST_USED_CATEGORY))
                .map(UUID::fromString)
                .orElse(null),
            rs.getBigDecimal(Columns.UNCATEGORIZED_AMOUNT),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE)));
  }
}
