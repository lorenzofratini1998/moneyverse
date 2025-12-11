package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.enums.TransactionEventStateEnum;
import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionEventBufferRepository
    extends JpaRepository<TransactionEventBuffer, UUID> {

  @Query("SELECT t FROM TransactionEventBuffer t WHERE t.state = 'PENDING' OR t.state = 'ERROR'")
  List<TransactionEventBuffer> findAll(Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE FROM TransactionEventBuffer t WHERE t.eventId IN :eventIds")
  void deleteByEventIds(List<UUID> eventIds);

  @Modifying
  @Transactional
  @Query("UPDATE TransactionEventBuffer t SET t.state = :state WHERE t.eventId IN :eventIds")
  void updateStateByEventIds(List<UUID> eventIds, TransactionEventStateEnum state);
}
