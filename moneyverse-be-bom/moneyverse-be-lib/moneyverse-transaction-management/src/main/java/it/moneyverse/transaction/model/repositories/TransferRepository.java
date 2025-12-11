package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transfer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
  boolean existsByTransferIdAndUserId(UUID transferId, UUID userId);

  @Query(
      "SELECT t FROM Transfer t WHERE t.transactionFrom.userId = :userId AND t.transactionTo.userId = :userId")
  List<Transfer> findTransferByUserId(UUID userId);

  @Query(
      "SELECT t FROM Transfer t WHERE t.transactionFrom.accountId = :accountId OR t.transactionTo.accountId = :accountId")
  List<Transfer> findTransferByAccountId(UUID accountId);
}
