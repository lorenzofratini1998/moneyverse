package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Transaction;
import java.util.List;
import java.util.UUID;

public interface TransactionCustomRepository {
  List<Transaction> findTransactions(UUID userId, TransactionCriteria param);

  Long count(UUID userId, TransactionCriteria param);
}
