package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, TransactionCustomRepository {}
