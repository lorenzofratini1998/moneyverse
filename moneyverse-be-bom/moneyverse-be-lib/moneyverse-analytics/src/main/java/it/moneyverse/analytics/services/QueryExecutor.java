package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.queries.Query;
import java.util.List;
import java.util.Optional;

public interface QueryExecutor {
  <T, P> List<T> execute(Query<T, P> query, P parameters);

  <T, P> Optional<T> executeSingleResult(Query<T, P> query, P parameters);
}
