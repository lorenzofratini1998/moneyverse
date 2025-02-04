package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transaction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, TransactionCustomRepository {
  boolean existsByUserIdAndTransactionId(UUID userId, UUID transactionId);

  List<Transaction> findTransactionByUserId(UUID userId);

  List<Transaction> findTransactionByAccountId(UUID accountId);
  List<Transaction> findTransactionByBudgetId(UUID budgetId);
}
