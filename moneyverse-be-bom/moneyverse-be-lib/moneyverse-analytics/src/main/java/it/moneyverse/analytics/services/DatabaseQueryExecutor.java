package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.queries.Query;
import it.moneyverse.analytics.model.repositories.TransactionEventRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DatabaseQueryExecutor implements QueryExecutor {

  private final TransactionEventRepository repository;

  public DatabaseQueryExecutor(TransactionEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public <T, P> List<T> execute(Query<T, P> query, P parameters) {
    return repository.executeQuery(query, parameters);
  }

  @Override
  public <T, P> Optional<T> executeSingleResult(Query<T, P> query, P parameters) {
    return repository.executeQuerySingleResult(query, parameters);
  }
}
