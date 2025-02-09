package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Transfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {}
