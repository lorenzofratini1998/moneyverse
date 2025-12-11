package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.model.projections.OverviewAnalyticsProjection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class OverviewAnalyticsQuery extends AbstractFilterQuery<OverviewAnalyticsProjection> {

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
                        argMax(t.DATE, t.EVENT_TIMESTAMP) AS DATE,
                        argMax(t.EVENT_TYPE, t.EVENT_TIMESTAMP) AS LAST_EVENT_TYPE
                     FROM TRANSACTION_EVENTS t
                     WHERE t.USER_ID = :userId
                     GROUP BY coalesce(t.ORIGINAL_TRANSACTION_ID, t.TRANSACTION_ID)
                     HAVING LAST_EVENT_TYPE != 2
                    )

                    SELECT
                        toStartOfMonth(DATE) as START_DATE,
                        toLastDayOfMonth(DATE) as END_DATE,
                        sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT > 0) AS TOTAL_INCOME,
                        abs(sumIf(NORMALIZED_AMOUNT, NORMALIZED_AMOUNT < 0)) AS TOTAL_EXPENSE,
                        sum(NORMALIZED_AMOUNT) AS TOTAL_AMOUNT
                    FROM filtered_transactions
                    GROUP BY START_DATE, END_DATE
                    ORDER BY START_DATE, END_DATE
                """;
  }

  @Override
  public RowMapper<OverviewAnalyticsProjection> getRowMapper() {
    return ((rs, rowNum) ->
        new OverviewAnalyticsProjection(
            rs.getDate(OverviewAnalyticsQuery.Columns.START_DATE).toLocalDate(),
            rs.getDate(OverviewAnalyticsQuery.Columns.END_DATE).toLocalDate(),
            rs.getBigDecimal(OverviewAnalyticsQuery.Columns.TOTAL_INCOME),
            rs.getBigDecimal(OverviewAnalyticsQuery.Columns.TOTAL_EXPENSE),
            rs.getBigDecimal(OverviewAnalyticsQuery.Columns.TOTAL_AMOUNT)));
  }

  private static class Columns {
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String TOTAL_INCOME = "TOTAL_INCOME";
    public static final String TOTAL_EXPENSE = "TOTAL_EXPENSE";
    public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";

    private Columns() {}
  }
}
