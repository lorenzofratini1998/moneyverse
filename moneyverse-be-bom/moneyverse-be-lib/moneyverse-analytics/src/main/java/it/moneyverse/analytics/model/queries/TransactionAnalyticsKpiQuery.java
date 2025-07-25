package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsKpiProjection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsKpiQuery
    extends AbstractFilterQuery<TransactionAnalyticsKpiProjection> {

  private static class Columns {
    public static final String NUMBER_OF_TRANSACTIONS = "NUMBER_OF_TRANSACTIONS";
    public static final String TOTAL_INCOME = "TOTAL_INCOME";
    public static final String TOTAL_EXPENSE = "TOTAL_EXPENSE";
    public static final String AVERAGE_AMOUNT = "AVERAGE_AMOUNT";
    public static final String PERCENTILE_90 = "PERCENTILE_90";
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
        )

        SELECT
            count(TRANSACTION_ID) AS NUMBER_OF_TRANSACTIONS,
            sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
            sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0) AS TOTAL_EXPENSE,
            avg(NORMALIZED_AMOUNT) AS AVERAGE_AMOUNT,
            quantile(0.9)(NORMALIZED_AMOUNT) AS PERCENTILE_90,
            'CURRENT' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE DATE BETWEEN :startDate AND :endDate

        UNION ALL

        SELECT
            count(TRANSACTION_ID) AS NUMBER_OF_TRANSACTIONS,
            sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
            sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0) AS TOTAL_EXPENSE,
            avg(NORMALIZED_AMOUNT) AS AVERAGE_AMOUNT,
            quantile(0.9)(NORMALIZED_AMOUNT) AS PERCENTILE_90,
            'COMPARE' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
""";
  }

  @Override
  public RowMapper<TransactionAnalyticsKpiProjection> getRowMapper() {
    return ((rs, rowNum) ->
        new TransactionAnalyticsKpiProjection(
            rs.getInt(Columns.NUMBER_OF_TRANSACTIONS),
            rs.getBigDecimal(Columns.TOTAL_INCOME),
            rs.getBigDecimal(Columns.TOTAL_EXPENSE),
            rs.getBigDecimal(Columns.AVERAGE_AMOUNT),
            rs.getBigDecimal(Columns.PERCENTILE_90),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE))));
  }
}
