package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.queries.Query;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ClickhouseTransactionEventRepository implements TransactionEventRepository {

  private static final String INSERT_QUERY =
      """
          INSERT INTO TRANSACTION_EVENTS (EVENT_ID, EVENT_TYPE, USER_ID, TRANSACTION_ID, ORIGINAL_TRANSACTION_ID, ACCOUNT_ID, CATEGORY_ID, BUDGET_ID, TAGS, AMOUNT, NORMALIZED_AMOUNT, CURRENCY, DATE, EVENT_TIMESTAMP)
          VALUES (:eventId, :eventType, :userId, :transactionId, :originalTransactionId, :accountId, :categoryId, :budgetId, :tags, :amount, :normalizedAmount, :currency, :date, :eventTimestamp)
          """;

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ClickhouseTransactionEventRepository(
      @Qualifier("clickHouseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void saveAll(List<TransactionEvent> transactionEvents) {
    if (transactionEvents.isEmpty()) {
      return;
    }
    SqlParameterSource[] params =
        transactionEvents.stream()
            .map(this::createParameterSource)
            .toArray(SqlParameterSource[]::new);

    jdbcTemplate.batchUpdate(INSERT_QUERY, params);
  }

  private SqlParameterSource createParameterSource(TransactionEvent event) {
    MapSqlParameterSource paramSource = new MapSqlParameterSource();
    paramSource.addValue("eventId", event.getEventId());
    paramSource.addValue("eventType", event.getEventType());
    paramSource.addValue("userId", event.getUserId());
    paramSource.addValue("transactionId", event.getTransactionId());
    paramSource.addValue("originalTransactionId", event.getOriginalTransactionId());
    paramSource.addValue("accountId", event.getAccountId());
    paramSource.addValue("categoryId", event.getCategoryId());
    paramSource.addValue("budgetId", event.getBudgetId());

    UUID[] tagsArray = event.getTags() != null ? event.getTags().toArray(new UUID[0]) : new UUID[0];
    paramSource.addValue("tags", tagsArray);

    paramSource.addValue("amount", event.getAmount());
    paramSource.addValue("normalizedAmount", event.getNormalizedAmount());
    paramSource.addValue("currency", event.getCurrency());
    paramSource.addValue("date", event.getDate());
    paramSource.addValue("eventTimestamp", event.getEventTimestamp());

    return paramSource;
  }

  @Override
  public <T, P> List<T> executeQuery(@Nonnull Query<T, P> query, @Nonnull P params) {
    return executeJdbcQuery(query, params);
  }

  @Override
  public <T, P> Optional<T> executeQuerySingleResult(
      @Nonnull Query<T, P> query, @Nonnull P params) {
    return executeJdbcQuery(query, params).stream().findFirst();
  }

  private <T, P> List<T> executeJdbcQuery(@Nonnull Query<T, P> query, @Nonnull P params) {
    return jdbcTemplate.query(query.getSql(), query.getParameters(params), query.getRowMapper());
  }
}
