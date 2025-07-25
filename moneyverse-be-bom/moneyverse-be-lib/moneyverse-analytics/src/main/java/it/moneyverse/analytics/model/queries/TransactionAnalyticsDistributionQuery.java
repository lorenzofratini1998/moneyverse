package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsDistributionProjection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsDistributionQuery
    extends AbstractFilterQuery<TransactionAnalyticsDistributionProjection> {

  private static class Columns {
    public static final String AMOUNT_RANGE = "AMOUNT_RANGE";
    public static final String TRANSACTION_COUNT = "TRANSACTION_COUNT";
    public static final String PERIOD_TYPE = "PERIOD_TYPE";

    private Columns() {}
  }

  @Override
  public String getSql() {
    return """
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
            multiIf(
                abs(NORMALIZED_AMOUNT) < 5, '< 5',
                abs(NORMALIZED_AMOUNT) >= 5 AND abs(NORMALIZED_AMOUNT) < 10, '5 - 10',
                abs(NORMALIZED_AMOUNT) >= 10 AND abs(NORMALIZED_AMOUNT) < 25, '10 - 25',
                abs(NORMALIZED_AMOUNT) >= 25 AND abs(NORMALIZED_AMOUNT) < 50, '25 - 50',
                abs(NORMALIZED_AMOUNT) >= 50 AND abs(NORMALIZED_AMOUNT) < 100, '50 - 100',
                abs(NORMALIZED_AMOUNT) >= 100 AND abs(NORMALIZED_AMOUNT) < 250, '100 - 250',
                abs(NORMALIZED_AMOUNT) >= 250 AND abs(NORMALIZED_AMOUNT) < 500, '250 - 500',
                '> 500'
            ) AS AMOUNT_RANGE,
        count() AS TRANSACTION_COUNT,
        'CURRENT' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE DATE BETWEEN :startDate AND :endDate AND NORMALIZED_AMOUNT < 0
        GROUP BY AMOUNT_RANGE, PERIOD_TYPE

        UNION ALL

        SELECT
            multiIf(
                abs(NORMALIZED_AMOUNT) < 5, '< 5',
                abs(NORMALIZED_AMOUNT) >= 5 AND abs(NORMALIZED_AMOUNT) < 10, '5 - 10',
                abs(NORMALIZED_AMOUNT) >= 10 AND abs(NORMALIZED_AMOUNT) < 25, '10 - 25',
                abs(NORMALIZED_AMOUNT) >= 25 AND abs(NORMALIZED_AMOUNT) < 50, '25 - 50',
                abs(NORMALIZED_AMOUNT) >= 50 AND abs(NORMALIZED_AMOUNT) < 100, '50 - 100',
                abs(NORMALIZED_AMOUNT) >= 100 AND abs(NORMALIZED_AMOUNT) < 250, '100 - 250',
                abs(NORMALIZED_AMOUNT) >= 250 AND abs(NORMALIZED_AMOUNT) < 500, '250 - 500',
                '> 500'
            ) AS AMOUNT_RANGE,
        count() AS TRANSACTION_COUNT,
        'COMPARE' AS PERIOD_TYPE
        FROM filtered_transactions
        WHERE :hasComparePeriod = 1 AND DATE BETWEEN :compareStartDate AND :compareEndDate AND NORMALIZED_AMOUNT < 0
        GROUP BY AMOUNT_RANGE, PERIOD_TYPE
        """;
  }

  @Override
  public RowMapper<TransactionAnalyticsDistributionProjection> getRowMapper() {
    return ((rs, rowNum) ->
        new TransactionAnalyticsDistributionProjection(
            rs.getString(Columns.AMOUNT_RANGE),
            rs.getInt(Columns.TRANSACTION_COUNT),
            QueryPeriodTypeEnum.valueOf(rs.getString(Columns.PERIOD_TYPE))));
  }
}

/*
multiIf(
       abs(NORMALIZED_AMOUNT) < 5, 1,
       abs(NORMALIZED_AMOUNT) < 10, 2,
       abs(NORMALIZED_AMOUNT) < 25, 3,
       abs(NORMALIZED_AMOUNT) < 50, 4,
       abs(NORMALIZED_AMOUNT) < 100, 5,
       abs(NORMALIZED_AMOUNT) < 250, 6,
       abs(NORMALIZED_AMOUNT) < 500, 7,
       8
   ) AS SORT_ORDER,
*/
