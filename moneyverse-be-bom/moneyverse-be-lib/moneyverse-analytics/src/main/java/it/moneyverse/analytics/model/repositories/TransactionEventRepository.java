package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.queries.Query;
import java.util.List;
import java.util.Optional;

public interface TransactionEventRepository {
  void saveAll(List<TransactionEvent> transactionEvents);

  <T, P> List<T> executeQuery(Query<T, P> query, P params);

  <T, P> Optional<T> executeQuerySingleResult(Query<T, P> query, P params);
}
