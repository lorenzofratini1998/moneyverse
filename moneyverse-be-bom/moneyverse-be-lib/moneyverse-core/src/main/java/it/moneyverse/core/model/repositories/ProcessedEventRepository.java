package it.moneyverse.core.model.repositories;

import it.moneyverse.core.model.entities.ProcessedEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {}
