package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Transaction;
import java.util.List;

public interface TransactionCustomRepository {
  List<Transaction> findTransactions(TransactionCriteria param);
}
