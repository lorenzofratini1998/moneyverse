package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsTrendProjection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsTrendQuery
    extends AbstractFilterQuery<TransactionAnalyticsTrendProjection> {

  private static class Columns {
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String EXPENSE = "EXPENSE";
    public static final String INCOME = "INCOME";
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
        argMax(t.DATE, t.EVENT_TIMESTAMP) AS DATE,
        argMax(t.EVENT_TYPE, t.EVENT_TIMESTAMP) AS LAST_EVENT_TYPE
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
     GROUP BY coalesce(t.ORIGINAL_TRANSACTION_ID, t.TRANSACTION_ID)
     HAVING LAST_EVENT_TYPE != 2
    )

    SELECT
        toStartOfMonth(DATE) AS START_DATE,
        toLastDayOfMonth(DATE) AS END_DATE,
        abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS EXPENSE,
        sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS INCOME,
        'CURRENT' AS PERIOD_TYPE
    FROM filtered_transactions
    WHERE DATE BETWEEN :startDate AND :endDate
    GROUP BY START_DATE, END_DATE
    ORDER BY START_DATE, END_DATE

    UNION ALL

    SELECT
        toStartOfMonth(DATE) AS START_DATE,
        toLastDayOfMonth(DATE) AS END_DATE,
        abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS EXPENSE,
        sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS INCOME,
        'COMPARE' AS PERIOD_TYPE
    FROM filtered_transactions
    WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate
    GROUP BY START_DATE, END_DATE
    ORDER BY START_DATE, END_DATE
""";
  }

  @Override
  public RowMapper<TransactionAnalyticsTrendProjection> getRowMapper() {
    return (rs, rowNum) ->
        new TransactionAnalyticsTrendProjection(
            rs.getDate(Columns.START_DATE).toLocalDate(),
            rs.getDate(Columns.END_DATE).toLocalDate(),
            rs.getBigDecimal(Columns.EXPENSE),
            rs.getBigDecimal(Columns.INCOME),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE)));
  }
}
