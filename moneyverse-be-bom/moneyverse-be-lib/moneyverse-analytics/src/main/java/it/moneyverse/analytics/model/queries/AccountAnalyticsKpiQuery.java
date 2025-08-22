package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsKpiQuery extends AbstractFilterQuery<AccountAnalyticsKpiProjection> {

  private static class Columns {
    public static final String TOTAL_EXPENSE = "TOTAL_EXPENSE";
    public static final String TOTAL_INCOME = "TOTAL_INCOME";
    public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    public static final String ACTIVE_ACCOUNTS = "ACTIVE_ACCOUNTS";
    public static final String MOST_USED_ACTIVE_ACCOUNT = "MOST_USED_ACTIVE_ACCOUNT";
    public static final String LEAST_USED_ACTIVE_ACCOUNT = "LEAST_USED_ACTIVE_ACCOUNT";
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
        current_account_stats AS (
         SELECT
             ACCOUNT_ID,
             count() AS transaction_count
         FROM filtered_transactions
         WHERE DATE BETWEEN :startDate AND :endDate
         GROUP BY ACCOUNT_ID
         HAVING transaction_count > 0
        ),
        compare_account_stats AS (
         SELECT
             ACCOUNT_ID,
             count() AS transaction_count
         FROM filtered_transactions
         WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
         GROUP BY ACCOUNT_ID
         HAVING transaction_count > 0
        )

        SELECT
         abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS TOTAL_EXPENSE,
         sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
         sum(NORMALIZED_AMOUNT) AS TOTAL_AMOUNT,
         uniqExact(ACCOUNT_ID) AS ACTIVE_ACCOUNTS,
         (SELECT ACCOUNT_ID FROM current_account_stats ORDER BY transaction_count DESC LIMIT 1) AS MOST_USED_ACTIVE_ACCOUNT,
         (SELECT ACCOUNT_ID FROM current_account_stats ORDER BY transaction_count LIMIT 1) AS LEAST_USED_ACTIVE_ACCOUNT,
         'CURRENT' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE DATE BETWEEN :startDate AND :endDate

        UNION ALL

        SELECT
         abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS TOTAL_EXPENSE,
         sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
         sum(NORMALIZED_AMOUNT) AS TOTAL_AMOUNT,
         uniqExact(ACCOUNT_ID) AS ACTIVE_ACCOUNTS,
         (SELECT ACCOUNT_ID FROM compare_account_stats ORDER BY transaction_count DESC LIMIT 1) AS MOST_USED_ACTIVE_ACCOUNT,
         (SELECT ACCOUNT_ID FROM compare_account_stats ORDER BY transaction_count LIMIT 1) AS LEAST_USED_ACTIVE_ACCOUNT,
         'COMPARE' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
""";
  }

  @Override
  public RowMapper<AccountAnalyticsKpiProjection> getRowMapper() {
    return (rs, rowNum) ->
        new AccountAnalyticsKpiProjection(
            rs.getBigDecimal(Columns.TOTAL_EXPENSE),
            rs.getBigDecimal(Columns.TOTAL_INCOME),
            rs.getBigDecimal(Columns.TOTAL_AMOUNT),
            rs.getInt(Columns.ACTIVE_ACCOUNTS),
            Optional.ofNullable(rs.getString(Columns.MOST_USED_ACTIVE_ACCOUNT))
                .map(UUID::fromString)
                .orElse(null),
            Optional.ofNullable(rs.getString(Columns.LEAST_USED_ACTIVE_ACCOUNT))
                .map(UUID::fromString)
                .orElse(null),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE)));
  }
}
