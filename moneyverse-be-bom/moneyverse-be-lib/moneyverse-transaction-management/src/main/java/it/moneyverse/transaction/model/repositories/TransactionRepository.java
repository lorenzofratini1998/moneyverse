package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, TransactionCustomRepository {

  boolean existsByUsernameAndTransactionId(String username, UUID transactionId);
}
