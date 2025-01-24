package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, TransactionCustomRepository {
  boolean existsByUsernameAndTransactionId(String username, UUID transactionId);
  List<Transaction> findTransactionByUsername(String username);
  List<Transaction> findTransactionByAccountId(UUID accountId);
}
